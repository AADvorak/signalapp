package com.example.signalapp;

import com.example.signalapp.dto.request.LoginDtoRequest;
import com.example.signalapp.dto.response.FieldErrorDtoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "/test.properties")
public class LoginLogoutIntegrationTest extends IntegrationTestWithRecaptcha {

    @BeforeAll
    public void beforeAll() throws Exception {
        userRepository.deleteAll();
        registerUsers();
        prepareRecaptchaVerifier();
    }

    @Test
    public void loginOk() {
        ResponseEntity<String> response = template.postForEntity(fullUrl(SESSIONS_URL),
                new LoginDtoRequest().setEmail(email1).setPassword(password), String.class);
        assertAll(() -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertNotNull(response.getHeaders().get("Set-Cookie")));
        // todo check db
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
    public void loginWithProperRecaptchaToken() {
        applicationProperties.setVerifyCaptcha(true);
        ResponseEntity<String> response = template.postForEntity(fullUrl(SESSIONS_URL),
                new LoginDtoRequest().setEmail(email1).setPassword(password).setToken(PROPER_TOKEN), String.class);
        assertAll(() -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertNotNull(response.getHeaders().get("Set-Cookie")));
        // todo check db
        applicationProperties.setVerifyCaptcha(false);
    }

    // todo
//    @Test
//    public void loginWithWrongRecaptchaToken() throws JsonProcessingException {
//        applicationProperties.setVerifyCaptcha(true);
//        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
//                () -> template.postForEntity(fullUrl(SESSIONS_URL),
//                        new LoginDtoRequest().setEmail(email1).setPassword(password).setToken(WRONG_TOKEN), String.class));
//        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
//        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
//                () -> assertEquals("RECAPTCHA_TOKEN_NOT_VERIFIED", error.getCode()),
//                () -> assertEquals("token", error.getField()));
//        applicationProperties.setVerifyCaptcha(false);
//    }

}
