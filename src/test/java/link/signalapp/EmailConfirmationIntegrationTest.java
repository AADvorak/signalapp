package link.signalapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import link.signalapp.dto.request.EmailConfirmDtoRequest;
import link.signalapp.dto.response.FieldErrorDtoResponse;
import link.signalapp.mail.MailTransport;
import link.signalapp.model.User;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "/test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmailConfirmationIntegrationTest extends IntegrationTestBase {

    private static final String CONFIRM_URL = "/confirm";

    @MockBean
    private MailTransport mailTransport;

    @Captor
    private ArgumentCaptor<String> emailCaptor;
    @Captor
    private ArgumentCaptor<String> subjectCaptor;
    @Captor
    private ArgumentCaptor<String> bodyCaptor;

    @BeforeAll
    public void clearAndRegisterUsers() {
        userRepository.deleteAll();
        registerUsers();
    }

    @Test
    public void confirmEmailOk() throws MessagingException {
        setEmailConfirmed(false);
        HttpHeaders headers = login(email1);
        ResponseEntity<String> response1 = template.exchange(fullUrl(USERS_URL + CONFIRM_URL),
                HttpMethod.POST, new HttpEntity<>(emailConfirmDtoRequest(), headers), String.class);
        verify(mailTransport).send(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());
        String confirmEmail = emailCaptor.getValue(), confirmUrl = bodyCaptor.getValue();
        assertAll(() -> assertEquals(email1, confirmEmail),
                () -> assertEquals(200, response1.getStatusCodeValue()));
        ResponseEntity<String> response2 = restTemplateNoRedirect().getForEntity(confirmUrl, String.class);
        User user = userRepository.findByEmail(email1);
        assertAll(() -> assertEquals(302, response2.getStatusCodeValue()),
                () -> assertEquals(origin() + "/user-settings",
                        Objects.requireNonNull(response2.getHeaders().get("Location")).get(0)),
                () -> assertTrue(user.isEmailConfirmed()));
    }

    @Test
    public void confirmEmailAlreadyConfirmed() throws JsonProcessingException {
        setEmailConfirmed(true);
        HttpHeaders headers = login(email1);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(USERS_URL + CONFIRM_URL),
                        HttpMethod.POST, new HttpEntity<>(emailConfirmDtoRequest(), headers), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("EMAIL_ALREADY_CONFIRMED", error.getCode()));
    }

    @Test
    public void confirmEmailUnauthorized() {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(USERS_URL + CONFIRM_URL),
                        HttpMethod.POST, new HttpEntity<>(emailConfirmDtoRequest()), String.class));
        assertEquals(401, exc.getRawStatusCode());
    }

    private void setEmailConfirmed(boolean confirmed) {
        User user = userRepository.findByEmail(email1);
        user.setEmailConfirmed(confirmed);
        userRepository.save(user);
    }

    private RestTemplate restTemplateNoRedirect() {
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        CloseableHttpClient build = HttpClientBuilder.create().disableRedirectHandling().build();
        factory.setHttpClient(build);
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }

    private EmailConfirmDtoRequest emailConfirmDtoRequest() {
        return new EmailConfirmDtoRequest()
                .setOrigin(origin())
                .setLocaleTitle("SignalApp - confirm email")
                .setLocaleMsg("$origin$/api/users/confirm/$code$");
    }

    private String origin() {
        return fullUrl("");
    }
}
