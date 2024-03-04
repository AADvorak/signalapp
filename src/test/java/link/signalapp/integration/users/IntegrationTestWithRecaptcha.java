package link.signalapp.integration.users;

import link.signalapp.captcha.RecaptchaVerifier;
import link.signalapp.error.SignalAppErrorCode;
import link.signalapp.error.SignalAppException;
import link.signalapp.integration.IntegrationTestBase;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

public class IntegrationTestWithRecaptcha extends IntegrationTestBase {

    protected static final String PROPER_TOKEN = "PROPER_TOKEN";
    protected static final String WRONG_TOKEN = "WRONG_TOKEN";

    @MockBean
    protected RecaptchaVerifier recaptchaVerifier;

    protected void prepareRecaptchaVerifier() throws Exception {
        doNothing().when(recaptchaVerifier).verify(PROPER_TOKEN);
        doThrow(new SignalAppException(SignalAppErrorCode.RECAPTCHA_TOKEN_NOT_VERIFIED, null))
                .when(recaptchaVerifier).verify(WRONG_TOKEN);
    }

}
