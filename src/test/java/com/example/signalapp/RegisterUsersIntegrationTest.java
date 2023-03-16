package com.example.signalapp;

import com.example.signalapp.dto.request.UserDtoRequest;
import com.example.signalapp.dto.response.FieldErrorDtoResponse;
import com.example.signalapp.dto.response.UserDtoResponse;
import com.example.signalapp.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RegisterUsersIntegrationTest extends IntegrationTestBase {

    @BeforeEach
    public void clearAllUsers() {
        userRepository.deleteAll();
    }

    @Test
    public void testRegisterOk() {
        UserDtoRequest userDtoRequest = createUser(email1);
        userDtoRequest.setPassword(RandomStringUtils.randomAlphanumeric(UserService.MAX_PASSWORD_LENGTH));
        ResponseEntity<UserDtoResponse> response = template.postForEntity(fullUrl(USERS_URL), userDtoRequest, UserDtoResponse.class);
        assertAll(() -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertEquals(userDtoRequest.getEmail(), Objects.requireNonNull(response.getBody()).getEmail()),
                () -> assertEquals(userDtoRequest.getFirstName(), Objects.requireNonNull(response.getBody()).getFirstName()),
                () -> assertEquals(userDtoRequest.getLastName(), Objects.requireNonNull(response.getBody()).getLastName()),
                () -> assertEquals(userDtoRequest.getPatronymic(), Objects.requireNonNull(response.getBody()).getPatronymic()),
                () -> assertFalse(Objects.requireNonNull(response.getBody()).isEmailConfirmed()));
    }

    @Test
    public void testRegisterNullNameOk() {
        UserDtoRequest userDtoRequest = createUser(email1);
        userDtoRequest.setFirstName(null);
        ResponseEntity<UserDtoResponse> response = template.postForEntity(fullUrl(USERS_URL), userDtoRequest, UserDtoResponse.class);
        assertAll(() -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertEquals(userDtoRequest.getEmail(), Objects.requireNonNull(response.getBody()).getEmail()),
                () -> assertEquals(userDtoRequest.getFirstName(), Objects.requireNonNull(response.getBody()).getFirstName()),
                () -> assertEquals(userDtoRequest.getLastName(), Objects.requireNonNull(response.getBody()).getLastName()),
                () -> assertEquals(userDtoRequest.getPatronymic(), Objects.requireNonNull(response.getBody()).getPatronymic()),
                () -> assertFalse(Objects.requireNonNull(response.getBody()).isEmailConfirmed()));
    }

    @Test
    public void testRegisterNullEmail() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser(null);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("NotEmpty", error.getCode()),
                () -> assertEquals("email", error.getField()));
    }

    @Test
    public void testRegisterEmptyEmail() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser("");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("NotEmpty", error.getCode()),
                () -> assertEquals("email", error.getField()));
    }

    @Test
    public void testRegisterInvalidEmail() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser("aaa");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("Email", error.getCode()),
                () -> assertEquals("email", error.getField()));
    }

    @Test
    public void testRegisterNullPassword() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser(email1).setPassword(null);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("MinLength", error.getCode()),
                () -> assertEquals("password", error.getField()));
    }

    @Test
    public void testRegisterEmptyPassword() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser(email1).setPassword("");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("MinLength", error.getCode()),
                () -> assertEquals("password", error.getField()));
    }

    @Test
    public void testRegisterShortPassword() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser(email1).setPassword("0000");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("MinLength", error.getCode()),
                () -> assertEquals("password", error.getField()));
    }

    @Test
    public void testRegisterLongPassword() throws JsonProcessingException {
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
    public void testRegisterLongName() throws JsonProcessingException {
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
    public void testRegisterEmailAlreadyExists() throws JsonProcessingException {
        UserDtoRequest userDtoRequest = createUser(email1);
        template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL), userDtoRequest, String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("EMAIL_ALREADY_EXISTS", error.getCode()),
                () -> assertEquals("Email already exists", error.getMessage()));
    }

}
