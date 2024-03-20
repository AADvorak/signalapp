package link.signalapp.integration.users;

import link.signalapp.dto.request.UserDtoRequest;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

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
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(userDtoRequest.getEmail(), userDtoResponse.getEmail()),
                () -> assertEquals(userDtoRequest.getFirstName(), userDtoResponse.getFirstName()),
                () -> assertEquals(userDtoRequest.getLastName(), userDtoResponse.getLastName()),
                () -> assertEquals(userDtoRequest.getPatronymic(), userDtoResponse.getPatronymic()),
                () -> assertFalse(userDtoResponse.isEmailConfirmed())
        );
    }

    @Test
    public void registerNullNameOk() {
        UserDtoRequest userDtoRequest = createUser(email1);
        userDtoRequest.setFirstName(null);
        ResponseEntity<UserDtoResponse> response = template.postForEntity(fullUrl(USERS_URL), userDtoRequest, UserDtoResponse.class);
        UserDtoResponse userDtoResponse = Objects.requireNonNull(response.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(userDtoRequest.getEmail(), userDtoResponse.getEmail()),
                () -> assertEquals(userDtoRequest.getFirstName(), userDtoResponse.getFirstName()),
                () -> assertEquals(userDtoRequest.getLastName(), userDtoResponse.getLastName()),
                () -> assertEquals(userDtoRequest.getPatronymic(), userDtoResponse.getPatronymic()),
                () -> assertFalse(Objects.requireNonNull(response.getBody()).isEmailConfirmed())
        );
    }

    @Test
    public void registerNullEmail() {
        UserDtoRequest userDtoRequest = createUser(null);
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class),
                "NotEmpty", "email");
    }

    @Test
    public void registerEmptyEmail() {
        UserDtoRequest userDtoRequest = createUser("");
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class),
                "NotEmpty", "email");
    }

    @Test
    public void registerInvalidEmail() {
        UserDtoRequest userDtoRequest = createUser("aaa");
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class),
                "Email", "email");
    }

    @Test
    public void registerNullPassword() {
        UserDtoRequest userDtoRequest = createUser(email1).setPassword(null);
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class),
                "MinLength", "password");
    }

    @Test
    public void registerEmptyPassword() {
        UserDtoRequest userDtoRequest = createUser(email1).setPassword("");
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class),
                "MinLength", "password");
    }

    @Test
    public void registerShortPassword() {
        UserDtoRequest userDtoRequest = createUser(email1).setPassword("0000");
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class),
                "MinLength", "password");
    }

    @Test
    public void registerLongPassword() {
        UserDtoRequest userDtoRequest = createUser(email1)
                .setPassword(RandomStringUtils.randomAlphanumeric(UserService.MAX_PASSWORD_LENGTH + 1));
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class),
                "Size", "password");
    }

    @Test
    public void registerLongName() {
        UserDtoRequest userDtoRequest = createUser(email1)
                .setFirstName(RandomStringUtils.randomAlphanumeric(applicationProperties.getMaxNameLength() + 1));
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class),
                "MaxLength", "firstName");
    }

    @Test
    public void registerEmailAlreadyExists() {
        UserDtoRequest userDtoRequest = createUser(email1);
        template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class);
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class),
                "EMAIL_ALREADY_EXISTS");
    }

    @Test
    public void registerWithProperRecaptchaToken() throws Exception {
        prepareRecaptchaVerifier();
        applicationProperties.setVerifyCaptcha(true);
        try {
            UserDtoRequest userDtoRequest = createUser(email1)
                    .setPassword(RandomStringUtils.randomAlphanumeric(UserService.MAX_PASSWORD_LENGTH))
                    .setToken(PROPER_TOKEN);
            ResponseEntity<UserDtoResponse> response = template.postForEntity(fullUrl(USERS_URL), userDtoRequest, UserDtoResponse.class);
            UserDtoResponse userDtoResponse = Objects.requireNonNull(response.getBody());
            assertAll(
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                    () -> assertEquals(userDtoRequest.getEmail(), userDtoResponse.getEmail()),
                    () -> assertEquals(userDtoRequest.getFirstName(), userDtoResponse.getFirstName()),
                    () -> assertEquals(userDtoRequest.getLastName(), userDtoResponse.getLastName()),
                    () -> assertEquals(userDtoRequest.getPatronymic(), userDtoResponse.getPatronymic()),
                    () -> assertFalse(userDtoResponse.isEmailConfirmed())
            );
        } finally {
            applicationProperties.setVerifyCaptcha(false);
        }
    }

    @Test
    public void registerWithWrongRecaptchaToken() throws Exception {
        prepareRecaptchaVerifier();
        applicationProperties.setVerifyCaptcha(true);
        try {
            UserDtoRequest userDtoRequest = createUser(email1)
                    .setPassword(RandomStringUtils.randomAlphanumeric(UserService.MAX_PASSWORD_LENGTH))
                    .setToken(WRONG_TOKEN);
            checkBadRequestError(
                    () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class),
                    "RECAPTCHA_TOKEN_NOT_VERIFIED");
        } finally {
            applicationProperties.setVerifyCaptcha(false);
        }
    }

}
