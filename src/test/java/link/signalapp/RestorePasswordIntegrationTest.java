package link.signalapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import link.signalapp.dto.request.RestorePasswordDtoRequest;
import link.signalapp.dto.response.FieldErrorDtoResponse;
import link.signalapp.model.User;
import link.signalapp.security.PasswordEncoder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;

import javax.mail.MessagingException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "/test.properties")
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
        assertAll(() -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertTrue(passwordEncoder.matches(arguments.getBody(), user.getPassword())));
    }

    @Test
    public void restorePasswordNotConfirmedEmail() throws JsonProcessingException {
        setEmailConfirmed(false);
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest(), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("email", error.getField()),
                () -> assertEquals("WRONG_EMAIL", error.getCode()));
    }

    @Test
    public void restorePasswordNotExistingEmail() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest().setEmail("notexisting@email"), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("email", error.getField()),
                () -> assertEquals("WRONG_EMAIL", error.getCode()));
    }

    @Test
    public void restorePasswordNotValidEmail() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest().setEmail("notvalid"), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("email", error.getField()),
                () -> assertEquals("Email", error.getCode()));
    }

    @Test
    public void restorePasswordEmptyEmail() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest().setEmail(""), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("email", error.getField()),
                () -> assertEquals("NotEmpty", error.getCode()));
    }

    @Test
    public void restorePasswordNullEmail() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest().setEmail(null), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("email", error.getField()),
                () -> assertEquals("NotEmpty", error.getCode()));
    }

    @Test
    public void restorePasswordEmptyLocaleTitle() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest().setLocaleTitle(""), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("localeTitle", error.getField()),
                () -> assertEquals("NotEmpty", error.getCode()));
    }

    @Test
    public void restorePasswordEmptyLocaleMsg() throws JsonProcessingException {
        HttpClientErrorException exc = assertThrows(HttpClientErrorException.class,
                () -> template.postForEntity(fullUrl(USERS_URL + RESTORE_URL),
                        restorePasswordDtoRequest().setLocaleMsg(""), String.class));
        FieldErrorDtoResponse error = mapper.readValue(exc.getResponseBodyAsString(), FieldErrorDtoResponse[].class)[0];
        assertAll(() -> assertEquals(400, exc.getRawStatusCode()),
                () -> assertEquals("localeMsg", error.getField()),
                () -> assertEquals("NotEmpty", error.getCode()));
    }

    // todo send email error test

    private RestorePasswordDtoRequest restorePasswordDtoRequest() {
        return new RestorePasswordDtoRequest()
                .setEmail(email1)
                .setLocaleTitle("Restore password")
                .setLocaleMsg("$newPassword$");
    }
}
