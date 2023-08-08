package link.signalapp;

import link.signalapp.dto.request.LoginDtoRequest;
import link.signalapp.dto.response.FieldErrorDtoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import link.signalapp.model.UserToken;
import link.signalapp.repository.UserTokenRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

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
        assertAll(() -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertEquals(tokenFromResponse, tokenFromRepository));
    }

    @Test
    public void loginWithEmptyEmail() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(SESSIONS_URL),
                        new LoginDtoRequest().setEmail("").setPassword(password), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("NotEmpty", error.getCode()),
                () -> assertEquals("email", error.getField()));
    }

    @Test
    public void loginWithEmptyPassword() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(SESSIONS_URL),
                        new LoginDtoRequest().setEmail(email1).setPassword(""), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("NotEmpty", error.getCode()),
                () -> assertEquals("password", error.getField()));
    }

    @Test
    public void loginWithWrongEmail() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(SESSIONS_URL),
                        new LoginDtoRequest().setEmail("wrong").setPassword(password), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("WRONG_EMAIL_PASSWORD", error.getCode()));
    }

    @Test
    public void loginWithWrongPassword() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(SESSIONS_URL),
                        new LoginDtoRequest().setEmail(email1).setPassword("wrong"), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("WRONG_EMAIL_PASSWORD", error.getCode()));
    }

    @Test
    public void logoutOk() {
        HttpHeaders headers = login(email1);
        ResponseEntity<String> response = template.exchange(fullUrl(SESSIONS_URL), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        assertEquals(response.getStatusCodeValue(), 200);
    }

    @Test
    public void logoutUnauthorized() {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class, () -> template.delete(fullUrl(SESSIONS_URL)));
        assertEquals(401, exc.getRawStatusCode());
    }

    @Test
    public void loginWithProperRecaptchaToken() throws Exception {
        prepareRecaptchaVerifier();
        applicationProperties.setVerifyCaptcha(true);
        ResponseEntity<String> response = template.postForEntity(fullUrl(SESSIONS_URL),
                new LoginDtoRequest().setEmail(email1).setPassword(password).setToken(PROPER_TOKEN), String.class);
        String tokenFromResponse = getTokenFromResponse(response);
        String tokenFromRepository = getTokenFromRepository();
        assertAll(() -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertEquals(tokenFromResponse, tokenFromRepository));
        applicationProperties.setVerifyCaptcha(false);
    }

    @Test
    public void loginWithWrongRecaptchaToken() throws Exception {
        prepareRecaptchaVerifier();
        applicationProperties.setVerifyCaptcha(true);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(SESSIONS_URL),
                        new LoginDtoRequest().setEmail(email1).setPassword(password).setToken(WRONG_TOKEN), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("RECAPTCHA_TOKEN_NOT_VERIFIED", error.getCode()),
                () -> assertEquals("token", error.getField()));
        applicationProperties.setVerifyCaptcha(false);
    }

    private String getTokenFromResponse(ResponseEntity<String> response) {
        return Objects.requireNonNull(response.getHeaders().get("Set-Cookie")).get(0).split(";")[0].split("=")[1];
    }

    private String getTokenFromRepository() {
        return userTokenRepository.findAll().stream()
                .filter(userToken -> email1.equals(userToken.getId().getUser().getEmail()))
                .max(Comparator.comparing(UserToken::getLastActionTime))
                .map(userToken -> userToken.getId().getToken()).orElse("");
    }

}
