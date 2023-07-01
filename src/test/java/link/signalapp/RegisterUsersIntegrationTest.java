package link.signalapp;

import link.signalapp.dto.request.UserDtoRequest;
import link.signalapp.dto.response.FieldErrorDtoResponse;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "/test.properties")
public class RegisterUsersIntegrationTest extends IntegrationTestWithRecaptcha {

    @BeforeEach
    public void clearAllUsers() {
        userRepository.deleteAll();
    }

    @Test
    public void registerOk() {
        UserDtoRequest userDtoRequest = createUser(email1);
        userDtoRequest.setPassword(RandomStringUtils.randomAlphanumeric(UserService.MAX_PASSWORD_LENGTH));
        ResponseEntity<UserDtoResponse> response = template.postForEntity(fullUrl(USERS_URL), userDtoRequest, UserDtoResponse.class);
        UserDtoResponse userDtoResponse = Objects.requireNonNull(response.getBody());
        assertAll(() -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertEquals(userDtoRequest.getEmail(), userDtoResponse.getEmail()),
                () -> assertEquals(userDtoRequest.getFirstName(), userDtoResponse.getFirstName()),
                () -> assertEquals(userDtoRequest.getLastName(), userDtoResponse.getLastName()),
                () -> assertEquals(userDtoRequest.getPatronymic(), userDtoResponse.getPatronymic()),
                () -> assertFalse(userDtoResponse.isEmailConfirmed()));
    }

    @Test
    public void registerNullNameOk() {
        UserDtoRequest userDtoRequest = createUser(email1);
        userDtoRequest.setFirstName(null);
        ResponseEntity<UserDtoResponse> response = template.postForEntity(fullUrl(USERS_URL), userDtoRequest, UserDtoResponse.class);
        UserDtoResponse userDtoResponse = Objects.requireNonNull(response.getBody());
        assertAll(() -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertEquals(userDtoRequest.getEmail(), userDtoResponse.getEmail()),
                () -> assertEquals(userDtoRequest.getFirstName(), userDtoResponse.getFirstName()),
                () -> assertEquals(userDtoRequest.getLastName(), userDtoResponse.getLastName()),
                () -> assertEquals(userDtoRequest.getPatronymic(), userDtoResponse.getPatronymic()),
                () -> assertFalse(Objects.requireNonNull(response.getBody()).isEmailConfirmed()));
    }

    @Test
    public void registerNullEmail() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser(null);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("NotEmpty", error.getCode()),
                () -> assertEquals("email", error.getField()));
    }

    @Test
    public void registerEmptyEmail() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser("");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("NotEmpty", error.getCode()),
                () -> assertEquals("email", error.getField()));
    }

    @Test
    public void registerInvalidEmail() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser("aaa");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("Email", error.getCode()),
                () -> assertEquals("email", error.getField()));
    }

    @Test
    public void registerNullPassword() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser(email1).setPassword(null);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("MinLength", error.getCode()),
                () -> assertEquals("password", error.getField()));
    }

    @Test
    public void registerEmptyPassword() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser(email1).setPassword("");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("MinLength", error.getCode()),
                () -> assertEquals("password", error.getField()));
    }

    @Test
    public void registerShortPassword() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser(email1).setPassword("0000");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("MinLength", error.getCode()),
                () -> assertEquals("password", error.getField()));
    }

    @Test
    public void registerLongPassword() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser(email1)
                .setPassword(RandomStringUtils.randomAlphanumeric(UserService.MAX_PASSWORD_LENGTH + 1));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("Size", error.getCode()),
                () -> assertEquals("password", error.getField()));
    }

    @Test
    public void registerLongName() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser(email1)
                .setFirstName(RandomStringUtils.randomAlphanumeric(applicationProperties.getMaxNameLength() + 1));
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("MaxLength", error.getCode()),
                () -> assertEquals("firstName", error.getField()));
    }

    @Test
    public void registerEmailAlreadyExists() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser(email1);
        template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("EMAIL_ALREADY_EXISTS", error.getCode()),
                () -> assertEquals("Email already exists", error.getMessage()));
    }

    @Test
    public void registerWithProperRecaptchaToken() throws Exception {
        prepareRecaptchaVerifier();
        applicationProperties.setVerifyCaptcha(true);
        UserDtoRequest userDtoRequest = createUser(email1)
                .setPassword(RandomStringUtils.randomAlphanumeric(UserService.MAX_PASSWORD_LENGTH))
                .setToken(PROPER_TOKEN);
        ResponseEntity<UserDtoResponse> response = template.postForEntity(fullUrl(USERS_URL), userDtoRequest, UserDtoResponse.class);
        UserDtoResponse userDtoResponse = Objects.requireNonNull(response.getBody());
        assertAll(() -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertEquals(userDtoRequest.getEmail(), userDtoResponse.getEmail()),
                () -> assertEquals(userDtoRequest.getFirstName(), userDtoResponse.getFirstName()),
                () -> assertEquals(userDtoRequest.getLastName(), userDtoResponse.getLastName()),
                () -> assertEquals(userDtoRequest.getPatronymic(), userDtoResponse.getPatronymic()),
                () -> assertFalse(userDtoResponse.isEmailConfirmed()));
        applicationProperties.setVerifyCaptcha(false);
    }

    @Test
    public void registerWithWrongRecaptchaToken() throws Exception {
        prepareRecaptchaVerifier();
        applicationProperties.setVerifyCaptcha(true);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL),
                        createUser(email1)
                                .setPassword(RandomStringUtils.randomAlphanumeric(UserService.MAX_PASSWORD_LENGTH))
                                .setToken(WRONG_TOKEN), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("RECAPTCHA_TOKEN_NOT_VERIFIED", error.getCode()),
                () -> assertEquals("token", error.getField()));
        applicationProperties.setVerifyCaptcha(false);
    }

}
