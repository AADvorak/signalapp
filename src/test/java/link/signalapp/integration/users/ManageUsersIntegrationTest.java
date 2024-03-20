package link.signalapp.integration.users;

import link.signalapp.dto.request.ChangePasswordDtoRequest;
import link.signalapp.dto.request.EditUserDtoRequest;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.integration.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

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
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(Objects.requireNonNull(response.getBody()).getEmail(), email1)
        );
    }

    @Test
    public void testGetUserInfoUnauthorized() {
        checkUnauthorizedError(() -> template.getForEntity(fullUrl(USERS_URL + ME_URL), String.class));
    }

    @Test
    public void testEditUserInfoOk() {
        HttpHeaders headers = login(email1);
        ResponseEntity<UserDtoResponse> response = template.exchange(fullUrl(USERS_URL + ME_URL),
                HttpMethod.GET, new HttpEntity<>(headers), UserDtoResponse.class);
        UserDtoResponse userDtoResponse = Objects.requireNonNull(response.getBody());
        EditUserDtoRequest editUserDtoRequest = new EditUserDtoRequest()
                .setEmail("1" + userDtoResponse.getEmail())
                .setFirstName(userDtoResponse.getFirstName() + "1")
                .setLastName(userDtoResponse.getLastName() + "1")
                .setPatronymic(userDtoResponse.getPatronymic() + "1");
        ResponseEntity<UserDtoResponse> response1 = template.exchange(fullUrl(USERS_URL + ME_URL),
                HttpMethod.PUT, new HttpEntity<>(editUserDtoRequest, headers), UserDtoResponse.class);
        UserDtoResponse userDtoResponse1 = Objects.requireNonNull(response1.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(userDtoResponse1.getEmail(), editUserDtoRequest.getEmail()),
                () -> assertEquals(userDtoResponse1.getFirstName(), editUserDtoRequest.getFirstName()),
                () -> assertEquals(userDtoResponse1.getLastName(), editUserDtoRequest.getLastName()),
                () -> assertEquals(userDtoResponse1.getPatronymic(), editUserDtoRequest.getPatronymic())
        );
    }

    @Test
    public void testEditUserInfoExistingEmail() {
        HttpHeaders headers = login(email1);
        ResponseEntity<UserDtoResponse> response = template.exchange(fullUrl(USERS_URL + ME_URL),
                HttpMethod.GET, new HttpEntity<>(headers), UserDtoResponse.class);
        UserDtoResponse userDtoResponse = Objects.requireNonNull(response.getBody());
        EditUserDtoRequest editUserDtoRequest = new EditUserDtoRequest()
                .setEmail(email2)
                .setFirstName(userDtoResponse.getFirstName() + "1")
                .setLastName(userDtoResponse.getLastName() + "1")
                .setPatronymic(userDtoResponse.getPatronymic() + "1");
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(USERS_URL + ME_URL), HttpMethod.PUT,
                        new HttpEntity<>(editUserDtoRequest, headers), UserDtoResponse.class),
                "EMAIL_ALREADY_EXISTS", "email");
    }

    @Test
    public void testEditUserInfoUnauthorized() {
        HttpHeaders headers = login(email1);
        ResponseEntity<UserDtoResponse> response = template.exchange(fullUrl(USERS_URL + ME_URL),
                HttpMethod.GET, new HttpEntity<>(headers), UserDtoResponse.class);
        UserDtoResponse userDtoResponse = Objects.requireNonNull(response.getBody());
        EditUserDtoRequest editUserDtoRequest = new EditUserDtoRequest()
                .setEmail(email2)
                .setFirstName(userDtoResponse.getFirstName() + "1")
                .setLastName(userDtoResponse.getLastName() + "1")
                .setPatronymic(userDtoResponse.getPatronymic() + "1");
        checkUnauthorizedError(() -> template.exchange(fullUrl(USERS_URL + ME_URL),
                HttpMethod.PUT, new HttpEntity<>(editUserDtoRequest), UserDtoResponse.class));
    }

    @Test
    public void testChangePasswordOk() {
        ChangePasswordDtoRequest changePasswordDtoRequest = new ChangePasswordDtoRequest()
                .setOldPassword(password)
                .setPassword(password + "1");
        ResponseEntity<String> response = template.exchange(fullUrl(USERS_URL + ME_URL + PASSWORD_URL),
                HttpMethod.PUT, new HttpEntity<>(changePasswordDtoRequest, login(email1)), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testChangePasswordUnauthorized() {
        ChangePasswordDtoRequest changePasswordDtoRequest = new ChangePasswordDtoRequest()
                .setOldPassword(password)
                .setPassword(password + "1");
        checkUnauthorizedError(() -> template.exchange(fullUrl(USERS_URL + ME_URL + PASSWORD_URL),
                HttpMethod.PUT, new HttpEntity<>(changePasswordDtoRequest), String.class));
    }

    @Test
    public void testDeleteUserOk() {
        ResponseEntity<String> response = template.exchange(fullUrl(USERS_URL + ME_URL),
                HttpMethod.DELETE, new HttpEntity<>(login(email1)), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // todo check all user data is deleted
    }

    @Test
    public void testDeleteUserUnauthorized() {
        checkUnauthorizedError(() -> template.delete(fullUrl(USERS_URL + ME_URL)));
    }

}
