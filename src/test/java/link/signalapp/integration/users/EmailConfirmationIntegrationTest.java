package link.signalapp.integration.users;

import link.signalapp.dto.request.EmailConfirmDtoRequest;
import link.signalapp.dto.response.ErrorDtoResponse;
import link.signalapp.model.User;
import link.signalapp.repository.UserConfirmRepository;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmailConfirmationIntegrationTest extends IntegrationTestWithEmail {

    private static final String CONFIRM_URL = "/confirm";

    @Autowired
    private UserConfirmRepository userConfirmRepository;

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
        CapturedEmailArguments arguments = captureEmailArguments();
        assertAll(
                () -> assertEquals(email1, arguments.getEmail()),
                () -> assertEquals(HttpStatus.OK, response1.getStatusCode())
        );
        ResponseEntity<String> response2 = restTemplateNoRedirect().getForEntity(arguments.getBody(), String.class);
        User user = userRepository.findByEmail(email1);
        assertAll(
                () -> assertEquals(HttpStatus.FOUND, response2.getStatusCode()),
                () -> assertEquals(origin() + "/user-settings",
                        Objects.requireNonNull(response2.getHeaders().get("Location")).get(0)),
                () -> assertTrue(user.isEmailConfirmed())
        );
    }

    @Test
    public void confirmEmailAlreadyConfirmed() {
        setEmailConfirmed(true);
        HttpHeaders headers = login(email1);
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(USERS_URL + CONFIRM_URL),
                        HttpMethod.POST, new HttpEntity<>(emailConfirmDtoRequest(), headers), String.class),
                "EMAIL_ALREADY_CONFIRMED");
    }

    @Test
    public void confirmEmailUnauthorized() {
        checkUnauthorizedError(
                () -> template.exchange(fullUrl(USERS_URL + CONFIRM_URL),
                        HttpMethod.POST, new HttpEntity<>(emailConfirmDtoRequest()), String.class));
    }

    @Test
    public void confirmEmailWrongCode() throws MessagingException {
        setEmailConfirmed(false);
        HttpHeaders headers = login(email1);
        ResponseEntity<String> response1 = template.exchange(fullUrl(USERS_URL + CONFIRM_URL),
                HttpMethod.POST, new HttpEntity<>(emailConfirmDtoRequest(), headers), String.class);
        CapturedEmailArguments arguments = captureEmailArguments();
        assertAll(
                () -> assertEquals(email1, arguments.getEmail()),
                () -> assertEquals(HttpStatus.OK, response1.getStatusCode())
        );
        String url = arguments.getBody().substring(0, arguments.getBody().length() - 2);
        ResponseEntity<String> response2 = restTemplateNoRedirect().getForEntity(url, String.class);
        User user = userRepository.findByEmail(email1);
        assertAll(
                () -> assertEquals(HttpStatus.FOUND, response2.getStatusCode()),
                () -> assertEquals(origin() + "/email-confirm-error",
                        Objects.requireNonNull(response2.getHeaders().get("Location")).get(0)),
                () -> assertFalse(user.isEmailConfirmed())
        );
    }

    @Test
    public void confirmEmailEmptyOrigin() {
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(USERS_URL + CONFIRM_URL),
                        HttpMethod.POST, new HttpEntity<>(emailConfirmDtoRequest().setOrigin("")), String.class),
                "NotEmpty", "origin");
    }

    @Test
    public void confirmEmailEmptyLocaleTitle() {
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(USERS_URL + CONFIRM_URL),
                        HttpMethod.POST, new HttpEntity<>(emailConfirmDtoRequest().setLocaleTitle("")), String.class),
                "NotEmpty", "localeTitle");
    }

    @Test
    public void confirmEmailEmptyLocaleMsg() {
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(USERS_URL + CONFIRM_URL),
                        HttpMethod.POST, new HttpEntity<>(emailConfirmDtoRequest().setLocaleMsg("")), String.class),
                "NotEmpty", "localeMsg");
    }

    @Disabled
    @Test
    public void confirmEmailThrowMessagingException() throws MessagingException {
        setEmailConfirmed(false);
        prepareMailTransportThrowMessagingException();
        HttpHeaders headers = login(email1);
        HttpServerErrorException exc = assertThrows(HttpServerErrorException.class,
                () -> template.exchange(fullUrl(USERS_URL + CONFIRM_URL),
                        HttpMethod.POST, new HttpEntity<>(emailConfirmDtoRequest(), headers), String.class));
        ErrorDtoResponse error = Objects.requireNonNull(exc.getResponseBodyAs(ErrorDtoResponse[].class))[0];
        // todo true while must be false
        boolean userConfirmExists = userConfirmRepository.findAll().stream()
                .anyMatch(userConfirm -> email1.equals(userConfirm.getId().getUser().getEmail()));
        assertAll(() -> assertEquals(500, exc.getStatusCode().value()),
                () -> assertEquals("INTERNAL_SERVER_ERROR", error.getCode()),
                () -> assertFalse(userConfirmExists));
    }

    private RestTemplate restTemplateNoRedirect() {
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient build = HttpClientBuilder.create().disableRedirectHandling().build();
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
