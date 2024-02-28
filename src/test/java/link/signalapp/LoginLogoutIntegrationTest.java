package link.signalapp;

import link.signalapp.dto.request.LoginDtoRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import link.signalapp.model.UserToken;
import link.signalapp.repository.UserTokenRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import java.util.Comparator;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginLogoutIntegrationTest extends IntegrationTestWithRecaptcha {

    @Autowired
    private UserTokenRepository userTokenRepository;

    @BeforeAll
    public void beforeAll() {
        userRepository.deleteAll();
        registerUsers();
    }

    @Test
    public void loginOk() {
        ResponseEntity<String> response = template.postForEntity(fullUrl(SESSIONS_URL),
                new LoginDtoRequest().setEmail(email1).setPassword(password), String.class);
        String tokenFromResponse = getTokenFromResponse(response), tokenFromRepository = getTokenFromRepository();
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(tokenFromResponse, tokenFromRepository)
        );
    }

    @Test
    public void loginWithEmptyEmail() throws JsonProcessingException {
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(SESSIONS_URL),
                        new LoginDtoRequest().setEmail("").setPassword(password), String.class),
                "NotEmpty", "email");
    }

    @Test
    public void loginWithEmptyPassword() throws JsonProcessingException {
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(SESSIONS_URL),
                        new LoginDtoRequest().setEmail(email1).setPassword(""), String.class),
                "NotEmpty", "password");
    }

    @Test
    public void loginWithWrongEmail() throws JsonProcessingException {
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(SESSIONS_URL),
                        new LoginDtoRequest().setEmail("wrong").setPassword(password), String.class),
                "WRONG_EMAIL_PASSWORD");
    }

    @Test
    public void loginWithWrongPassword() throws JsonProcessingException {
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(SESSIONS_URL),
                        new LoginDtoRequest().setEmail(email1).setPassword("wrong"), String.class),
                "WRONG_EMAIL_PASSWORD");
    }

    @Test
    public void logoutOk() {
        HttpHeaders headers = login(email1);
        ResponseEntity<String> response = template.exchange(fullUrl(SESSIONS_URL), HttpMethod.DELETE,
                new HttpEntity<>(headers), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void logoutUnauthorized() {
        checkUnauthorizedError(() -> template.delete(fullUrl(SESSIONS_URL)));
    }

    @Test
    public void loginWithProperRecaptchaToken() throws Exception {
        prepareRecaptchaVerifier();
        applicationProperties.setVerifyCaptcha(true);
        try {
            LoginDtoRequest request = new LoginDtoRequest()
                    .setEmail(email1)
                    .setPassword(password)
                    .setToken(PROPER_TOKEN);
            ResponseEntity<String> response = template.postForEntity(fullUrl(SESSIONS_URL), request, String.class);
            String tokenFromResponse = getTokenFromResponse(response);
            String tokenFromRepository = getTokenFromRepository();
            assertAll(
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                    () -> assertEquals(tokenFromResponse, tokenFromRepository)
            );
        } finally {
            applicationProperties.setVerifyCaptcha(false);
        }
    }

    @Test
    public void loginWithWrongRecaptchaToken() throws Exception {
        prepareRecaptchaVerifier();
        applicationProperties.setVerifyCaptcha(true);
        try {
            LoginDtoRequest request = new LoginDtoRequest()
                    .setEmail(email1)
                    .setPassword(password)
                    .setToken(WRONG_TOKEN);
            checkBadRequestError(
                    () -> template.postForEntity(fullUrl(SESSIONS_URL), request, String.class),
                    "RECAPTCHA_TOKEN_NOT_VERIFIED");
        } finally {
            applicationProperties.setVerifyCaptcha(false);
        }
    }

    private String getTokenFromResponse(ResponseEntity<String> response) {
        return Objects.requireNonNull(response.getHeaders().get("Set-Cookie"))
                .get(0).split(";")[0].split("=")[1];
    }

    private String getTokenFromRepository() {
        return userTokenRepository.findAll().stream()
                .filter(userToken -> email1.equals(userToken.getId().getUser().getEmail()))
                .max(Comparator.comparing(UserToken::getLastActionTime))
                .map(userToken -> userToken.getId().getToken()).orElse("");
    }

}
