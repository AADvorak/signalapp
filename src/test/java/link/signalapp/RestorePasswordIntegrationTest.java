package link.signalapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import link.signalapp.dto.request.RestorePasswordDtoRequest;
import link.signalapp.dto.response.ErrorDtoResponse;
import link.signalapp.model.User;
import link.signalapp.security.PasswordEncoder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import javax.mail.MessagingException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestorePasswordIntegrationTest extends IntegrationTestWithEmail {

    private static final String RESTORE_URL = "/restore";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    public void clearAndRegisterUsers() {
        userRepository.deleteAll();
        registerUsers();
    }

    @Test
    public void restorePasswordOk() throws MessagingException {
        setEmailConfirmed(true);
        ResponseEntity<String> response = template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                restorePasswordDtoRequest(), String.class);
        CapturedEmailArguments arguments = captureEmailArguments();
        User user = userRepository.findByEmail(email1);
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertTrue(passwordEncoder.matches(arguments.getBody(), user.getPassword()))
        );
    }

    @Test
    public void restorePasswordNotConfirmedEmail() throws JsonProcessingException {
        setEmailConfirmed(false);
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest(), String.class),
                "WRONG_EMAIL", "email");
    }

    @Test
    public void restorePasswordNotExistingEmail() throws JsonProcessingException {
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest().setEmail("notexisting@email"), String.class),
                "WRONG_EMAIL", "email");
    }

    @Test
    public void restorePasswordNotValidEmail() throws JsonProcessingException {
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest().setEmail("notvalid"), String.class),
                "Email", "email");
    }

    @Test
    public void restorePasswordEmptyEmail() throws JsonProcessingException {
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest().setEmail(""), String.class),
                "NotEmpty", "email");
    }

    @Test
    public void restorePasswordNullEmail() throws JsonProcessingException {
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest().setEmail(null), String.class),
                "NotEmpty", "email");
    }

    @Test
    public void restorePasswordEmptyLocaleTitle() throws JsonProcessingException {
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest().setLocaleTitle(""), String.class),
                "NotEmpty", "localeTitle");
    }

    @Test
    public void restorePasswordEmptyLocaleMsg() throws JsonProcessingException {
        checkBadRequestFieldError(
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest().setLocaleMsg(""), String.class),
                "NotEmpty", "localeMsg");
    }

    @Test
    public void restorePasswordThrowMessagingException() throws JsonProcessingException, MessagingException {
        prepareMailTransportThrowMessagingException();
        setEmailConfirmed(true);
        String beforeRequestPassword = userRepository.findByEmail(email1).getPassword();
        HttpServerErrorException exc = assertThrows(HttpServerErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest(), String.class));
        ErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), ErrorDtoResponse[].class)[0];
        String afterRequestPassword = userRepository.findByEmail(email1).getPassword();
        assertAll(
                () -> assertEquals(500, exc.getStatusCode().value()),
                () -> assertEquals("INTERNAL_SERVER_ERROR", error.getCode()),
                () -> assertEquals(beforeRequestPassword, afterRequestPassword)
        );
    }

    private RestorePasswordDtoRequest restorePasswordDtoRequest() {
        return new RestorePasswordDtoRequest()
                .setEmail(email1)
                .setLocaleTitle("Restore password")
                .setLocaleMsg("$newPassword$");
    }
}
