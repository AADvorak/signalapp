package link.signalapp;

import link.signalapp.mail.MailTransport;
import link.signalapp.model.User;
import lombok.Data;
import lombok.experimental.Accessors;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.mail.MessagingException;

import static org.mockito.Mockito.verify;

public class IntegrationTestWithEmail extends IntegrationTestBase {

    @MockBean
    protected MailTransport mailTransport;

    @Captor
    protected ArgumentCaptor<String> emailCaptor;
    @Captor
    protected ArgumentCaptor<String> subjectCaptor;
    @Captor
    protected ArgumentCaptor<String> bodyCaptor;

    protected void setEmailConfirmed(boolean confirmed) {
        User user = userRepository.findByEmail(email1);
        user.setEmailConfirmed(confirmed);
        userRepository.save(user);
    }

    protected CapturedEmailArguments captureEmailArguments() throws MessagingException {
        verify(mailTransport).send(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());
        return new CapturedEmailArguments()
                .setEmail(emailCaptor.getValue())
                .setSubject(subjectCaptor.getValue())
                .setBody(bodyCaptor.getValue());
    }

    @Data
    @Accessors(chain = true)
    protected static class CapturedEmailArguments {
        private String email;
        private String subject;
        private String body;
    }

}
