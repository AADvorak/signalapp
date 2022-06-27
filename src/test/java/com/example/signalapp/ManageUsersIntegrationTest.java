package com.example.signalapp;

import com.example.signalapp.dto.request.ChangePasswordDtoRequest;
import com.example.signalapp.dto.request.EditUserDtoRequest;
import com.example.signalapp.dto.response.FieldErrorDtoResponse;
import com.example.signalapp.dto.response.UserDtoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ManageUsersIntegrationTest extends IntegrationTestBase {

    private static final String ME_URL = "/me";
    private static final String PASSWORD_URL = "/password";

    @BeforeEach
    public void clearAndRegisterUsers() {
        userRepository.deleteAll();
        registerUsers();
    }

    @Test
    public void testGetUserInfoOk() {
        ResponseEntity<UserDtoResponse> response = template.exchange(fullUrl(USERS_URL + ME_URL), HttpMethod.GET,
                new HttpEntity<>(login(email1)), UserDtoResponse.class);
        assertAll(() -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertEquals(Objects.requireNonNull(response.getBody()).getEmail(), email1));
    }

    @Test
    public void testGetUserInfoUnauthorized() {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.getForEntity(fullUrl(USERS_URL + ME_URL), String.class));
        assertEquals(401, exc.getRawStatusCode());
    }

    @Test
    public void testEditUserInfoOk() {
        HttpHeaders headers = login(email1);
        ResponseEntity<UserDtoResponse> response = template.exchange(fullUrl(USERS_URL + ME_URL), HttpMethod.GET,
                new HttpEntity<>(headers), UserDtoResponse.class);
        UserDtoResponse userDtoResponse = Objects.requireNonNull(response.getBody());
        EditUserDtoRequest editUserDtoRequest = new EditUserDtoRequest();
        editUserDtoRequest.setEmail("1" + userDtoResponse.getEmail());
        editUserDtoRequest.setFirstName(userDtoResponse.getFirstName() + "1");
        editUserDtoRequest.setLastName(userDtoResponse.getLastName() + "1");
        editUserDtoRequest.setPatronymic(userDtoResponse.getPatronymic() + "1");
        ResponseEntity<UserDtoResponse> response1 = template.exchange(fullUrl(USERS_URL + ME_URL), HttpMethod.PUT,
                new HttpEntity<>(editUserDtoRequest, headers), UserDtoResponse.class);
        UserDtoResponse userDtoResponse1 = Objects.requireNonNull(response1.getBody());
        assertAll(() -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertEquals(userDtoResponse1.getEmail(), editUserDtoRequest.getEmail()),
                () -> assertEquals(userDtoResponse1.getFirstName(), editUserDtoRequest.getFirstName()),
                () -> assertEquals(userDtoResponse1.getLastName(), editUserDtoRequest.getLastName()),
                () -> assertEquals(userDtoResponse1.getPatronymic(), editUserDtoRequest.getPatronymic()));
    }

    @Test
    public void testEditUserInfoExistingEmail() throws JsonProcessingException {
        HttpHeaders headers = login(email1);
        ResponseEntity<UserDtoResponse> response = template.exchange(fullUrl(USERS_URL + ME_URL), HttpMethod.GET,
                new HttpEntity<>(headers), UserDtoResponse.class);
        UserDtoResponse userDtoResponse = Objects.requireNonNull(response.getBody());
        EditUserDtoRequest editUserDtoRequest = new EditUserDtoRequest();
        editUserDtoRequest.setEmail(email2);
        editUserDtoRequest.setFirstName(userDtoResponse.getFirstName() + "1");
        editUserDtoRequest.setLastName(userDtoResponse.getLastName() + "1");
        editUserDtoRequest.setPatronymic(userDtoResponse.getPatronymic() + "1");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(USERS_URL + ME_URL), HttpMethod.PUT,
                new HttpEntity<>(editUserDtoRequest, headers), UserDtoResponse.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("email", error.getField()),
                () -> assertEquals("EMAIL_ALREADY_EXISTS", error.getCode()));
    }

    @Test
    public void testEditUserInfoUnauthorized() {
        HttpHeaders headers = login(email1);
        ResponseEntity<UserDtoResponse> response = template.exchange(fullUrl(USERS_URL + ME_URL), HttpMethod.GET,
                new HttpEntity<>(headers), UserDtoResponse.class);
        UserDtoResponse userDtoResponse = Objects.requireNonNull(response.getBody());
        EditUserDtoRequest editUserDtoRequest = new EditUserDtoRequest();
        editUserDtoRequest.setEmail(email2);
        editUserDtoRequest.setFirstName(userDtoResponse.getFirstName() + "1");
        editUserDtoRequest.setLastName(userDtoResponse.getLastName() + "1");
        editUserDtoRequest.setPatronymic(userDtoResponse.getPatronymic() + "1");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(USERS_URL + ME_URL), HttpMethod.PUT,
                        new HttpEntity<>(editUserDtoRequest), UserDtoResponse.class));
        assertEquals(401, exc.getRawStatusCode());
    }

    @Test
    public void testChangePasswordOk() {
        ChangePasswordDtoRequest changePasswordDtoRequest = new ChangePasswordDtoRequest();
        changePasswordDtoRequest.setOldPassword(password);
        changePasswordDtoRequest.setPassword(password + "1");
        ResponseEntity<String> response = template.exchange(fullUrl(USERS_URL + ME_URL + PASSWORD_URL),
                HttpMethod.PUT, new HttpEntity<>(changePasswordDtoRequest, login(email1)), String.class);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testChangePasswordUnauthorized() {
        ChangePasswordDtoRequest changePasswordDtoRequest = new ChangePasswordDtoRequest();
        changePasswordDtoRequest.setOldPassword(password);
        changePasswordDtoRequest.setPassword(password + "1");
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.exchange(fullUrl(USERS_URL + ME_URL + PASSWORD_URL),
                HttpMethod.PUT, new HttpEntity<>(changePasswordDtoRequest), String.class));
        assertEquals(401, exc.getRawStatusCode());
    }

    @Test
    public void testDeleteUserOk() {
        ResponseEntity<String> response = template.exchange(fullUrl(USERS_URL + ME_URL),
                HttpMethod.DELETE, new HttpEntity<>(login(email1)), String.class);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testDeleteUserUnauthorized() {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.delete(fullUrl(USERS_URL + ME_URL)));
        assertEquals(401, exc.getRawStatusCode());
    }

}