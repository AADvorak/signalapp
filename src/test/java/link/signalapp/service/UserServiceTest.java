package link.signalapp.service;

import link.signalapp.ApplicationProperties;
import link.signalapp.captcha.RecaptchaVerifier;
import link.signalapp.dto.request.*;
import link.signalapp.dto.response.ResponseWithToken;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.error.code.SignalAppDataErrorCode;
import link.signalapp.error.code.SignalAppErrorCode;
import link.signalapp.error.exception.SignalAppDataException;
import link.signalapp.error.exception.SignalAppException;
import link.signalapp.error.exception.SignalAppUnauthorizedException;
import link.signalapp.file.FileManager;
import link.signalapp.mail.MailTransport;
import link.signalapp.model.User;
import link.signalapp.model.UserConfirm;
import link.signalapp.model.UserToken;
import link.signalapp.repository.UserConfirmRepository;
import link.signalapp.repository.UserRepository;
import link.signalapp.repository.UserTokenRepository;
import link.signalapp.security.PasswordEncoder;
import link.signalapp.security.SignalAppUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.mail.MessagingException;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final PasswordEncoder PASSWORD_ENCODER = new PasswordEncoder(
            new ApplicationProperties().setSecureRandomSeed("seed")
    );

    private final Data data = new Data();

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserConfirmRepository userConfirmRepository;
    @Mock
    private MailTransport mailTransport;
    @Mock
    private FileManager fileManager;
    @Mock
    private RecaptchaVerifier recaptchaVerifier;
    @Mock
    private UserTokenRepository userTokenRepository;
    @Mock
    private ApplicationProperties applicationProperties;
    @Spy
    private final PasswordEncoder passwordEncoder = PASSWORD_ENCODER;

    @Captor
    private ArgumentCaptor<User> userCaptor;
    @Captor
    private ArgumentCaptor<String> tokenCaptor;
    @Captor
    private ArgumentCaptor<UserToken> userTokenCaptor;
    @Captor
    private ArgumentCaptor<UserConfirm> userConfirmCaptor;
    @Captor
    private ArgumentCaptor<String> rawPasswordCaptor;
    @Captor
    private ArgumentCaptor<Integer> idCaptor;
    @Captor
    private ArgumentCaptor<String> emailCaptor;
    @Captor
    private ArgumentCaptor<String> subjectCaptor;
    @Captor
    private ArgumentCaptor<String> bodyCaptor;

    @Test
    public void registerOk() {
        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
        when(userRepository.save(any(User.class))).then(returnsFirstArg());
        UserDtoRequest request = data.userDtoRequest();
        ResponseWithToken<UserDtoResponse> response = userService.register(request);
        verify(userRepository).save(userCaptor.capture());
        verify(recaptchaVerifier).verify(tokenCaptor.capture());
        verify(userTokenRepository).save(userTokenCaptor.capture());
        verify(passwordEncoder).encode(rawPasswordCaptor.capture());
        User capturedUser = userCaptor.getValue();
        String capturedToken = tokenCaptor.getValue();
        UserToken capturedUserToken = userTokenCaptor.getValue();
        String rawPassword = rawPasswordCaptor.getValue();
        assertAll(
                () -> assertEquals(capturedUserToken.getId().getToken(), response.getToken()),
                () -> assertEquals(request.getEmail(), response.getResponse().getEmail()),
                () -> assertEquals(request.getFirstName(), response.getResponse().getFirstName()),
                () -> assertEquals(request.getLastName(), response.getResponse().getLastName()),
                () -> assertEquals(request.getPatronymic(), response.getResponse().getPatronymic()),
                () -> assertEquals(request.getEmail(), capturedUser.getEmail()),
                () -> assertEquals(request.getPassword(), rawPassword),
                () -> assertNotNull(capturedUser.getPassword()),
                () -> assertEquals(request.getFirstName(), capturedUser.getFirstName()),
                () -> assertEquals(request.getLastName(), capturedUser.getLastName()),
                () -> assertEquals(request.getPatronymic(), capturedUser.getPatronymic()),
                () -> assertFalse(capturedUser.isEmailConfirmed()),
                () -> assertEquals(request.getToken(), capturedToken)
        );
    }

    @Test
    public void registerTokenNotVerifiedException() {
        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
        prepareThrowRecaptchaTokenNotVerifiedException();
        SignalAppException exc = assertThrows(SignalAppException.class,
                () -> userService.register(data.userDtoRequest()));
        assertEquals(SignalAppErrorCode.RECAPTCHA_TOKEN_NOT_VERIFIED, exc.getErrorCode());
    }

    @Test
    public void registerEmailAlreadyExistsException() {
        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
        doThrow(new DataIntegrityViolationException(""))
                .when(userRepository).save(any(User.class));
        SignalAppDataException exc = assertThrows(SignalAppDataException.class,
                () -> userService.register(data.userDtoRequest()));
        assertEquals(SignalAppDataErrorCode.EMAIL_ALREADY_EXISTS, exc.getErrorCode());
    }

    @Test
    public void loginOk() {
        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
        LoginDtoRequest request = data.loginDtoRequest();
        User user = data.userWithUnconfirmedEmail();
        when(userRepository.findByEmail(Data.EMAIL)).thenReturn(user);
        ResponseWithToken<UserDtoResponse> response = userService.login(request);
        verify(recaptchaVerifier).verify(tokenCaptor.capture());
        verify(userTokenRepository).save(userTokenCaptor.capture());
        String capturedToken = tokenCaptor.getValue();
        UserToken capturedUserToken = userTokenCaptor.getValue();
        assertAll(
                () -> assertEquals(capturedUserToken.getId().getToken(), response.getToken()),
                () -> assertEquals(user.getEmail(), response.getResponse().getEmail()),
                () -> assertEquals(user.isEmailConfirmed(), response.getResponse().isEmailConfirmed()),
                () -> assertEquals(user.getFirstName(), response.getResponse().getFirstName()),
                () -> assertEquals(user.getLastName(), response.getResponse().getLastName()),
                () -> assertEquals(user.getPatronymic(), response.getResponse().getPatronymic()),
                () -> assertEquals(request.getToken(), capturedToken)
        );
    }

    @Test
    public void loginTokenNotVerifiedException() {
        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
        prepareThrowRecaptchaTokenNotVerifiedException();
        SignalAppException exc = assertThrows(SignalAppException.class,
                () -> userService.login(data.loginDtoRequest()));
        assertEquals(SignalAppErrorCode.RECAPTCHA_TOKEN_NOT_VERIFIED, exc.getErrorCode());
    }

    @Test
    public void loginUserIsNull() {
        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
        when(userRepository.findByEmail(Data.EMAIL)).thenReturn(null);
        SignalAppDataException exc = assertThrows(SignalAppDataException.class,
                () -> userService.login(data.loginDtoRequest()));
        assertEquals(SignalAppDataErrorCode.WRONG_EMAIL_PASSWORD, exc.getErrorCode());
    }

    @Test
    public void loginPasswordNotMatches() {
        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
        when(userRepository.findByEmail(Data.EMAIL)).thenReturn(data.userWithUnconfirmedEmail());
        SignalAppDataException exc = assertThrows(SignalAppDataException.class,
                () -> userService.login(data.loginDtoRequest().setPassword("wrong")));
        assertEquals(SignalAppDataErrorCode.WRONG_EMAIL_PASSWORD, exc.getErrorCode());
    }

    @Test
    public void logoutOk() {
        String token = createToken();
        fillSecurityContextHolder(data.user(), token);
        when(userTokenRepository.deleteByToken(token)).thenReturn(1);
        assertDoesNotThrow(() -> userService.logout());
    }

    @Test
    public void logoutUnauthorizedException() {
        fillSecurityContextHolder();
        assertThrows(SignalAppUnauthorizedException.class, () -> userService.logout());
    }

    @Test
    public void deleteUserOk() throws SignalAppUnauthorizedException {
        String token = createToken();
        User user = data.userWithConfirmedEmail();
        fillSecurityContextHolder(user, token);
        when(userRepository.deleteByToken(token)).thenReturn(1);
        userService.deleteCurrentUser();
        verify(fileManager).deleteAllUserData(idCaptor.capture());
        assertEquals(Data.ID, idCaptor.getValue());
    }

    @Test
    public void deleteUserUnauthorizedException() {
        fillSecurityContextHolder();
        assertThrows(SignalAppUnauthorizedException.class, () -> userService.deleteCurrentUser());
    }

    @Test
    public void editCurrentUserWithoutEmailChangeOk() throws SignalAppUnauthorizedException, SignalAppDataException {
        User user = data.userWithConfirmedEmail();
        EditUserDtoRequest request = data.editUserDtoRequest(user, false);
        String token = createToken();
        fillSecurityContextHolder(user, token);
        when(userRepository.save(any(User.class))).then(returnsFirstArg());
        UserDtoResponse response = userService.editCurrentUser(request);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertAll(
                () -> assertEquals(user.getId(), capturedUser.getId()),
                () -> assertEquals(user.getPassword(), capturedUser.getPassword()),
                () -> assertEquals(request.getEmail(), capturedUser.getEmail()),
                () -> assertEquals(request.getFirstName(), capturedUser.getFirstName()),
                () -> assertEquals(request.getLastName(), capturedUser.getLastName()),
                () -> assertEquals(request.getPatronymic(), capturedUser.getPatronymic()),
                () -> assertTrue(capturedUser.isEmailConfirmed()),
                () -> assertEquals(user.getId(), response.getId()),
                () -> assertEquals(user.getEmail(), response.getEmail()),
                () -> assertEquals(user.getFirstName(), response.getFirstName()),
                () -> assertEquals(user.getLastName(), response.getLastName()),
                () -> assertEquals(user.getPatronymic(), response.getPatronymic()),
                () -> assertTrue(response.isEmailConfirmed())
        );
    }

    @Test
    public void editCurrentUserWithEmailChangeOk() throws SignalAppUnauthorizedException, SignalAppDataException {
        User user = data.userWithConfirmedEmail();
        EditUserDtoRequest request = data.editUserDtoRequest(user, true);
        String token = createToken();
        fillSecurityContextHolder(user, token);
        when(userRepository.save(any(User.class))).then(returnsFirstArg());
        UserDtoResponse response = userService.editCurrentUser(request);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertAll(
                () -> assertEquals(user.getId(), capturedUser.getId()),
                () -> assertEquals(user.getPassword(), capturedUser.getPassword()),
                () -> assertEquals(request.getEmail(), capturedUser.getEmail()),
                () -> assertEquals(request.getFirstName(), capturedUser.getFirstName()),
                () -> assertEquals(request.getLastName(), capturedUser.getLastName()),
                () -> assertEquals(request.getPatronymic(), capturedUser.getPatronymic()),
                () -> assertFalse(capturedUser.isEmailConfirmed()),
                () -> assertEquals(user.getId(), response.getId()),
                () -> assertEquals(user.getEmail(), response.getEmail()),
                () -> assertEquals(user.getFirstName(), response.getFirstName()),
                () -> assertEquals(user.getLastName(), response.getLastName()),
                () -> assertEquals(user.getPatronymic(), response.getPatronymic()),
                () -> assertFalse(response.isEmailConfirmed())
        );
    }

    @Test
    public void editCurrentUserUnauthorizedException() {
        fillSecurityContextHolder();
        assertThrows(SignalAppUnauthorizedException.class,
                () -> userService.editCurrentUser(data.editUserDtoRequest(data.userWithConfirmedEmail(), false)));
    }

    @Test
    public void editCurrentUserEmailAlreadyExistsException() {
        String token = createToken();
        User user = data.userWithConfirmedEmail();
        fillSecurityContextHolder(user, token);
        doThrow(new DataIntegrityViolationException(""))
                .when(userRepository).save(any(User.class));
        SignalAppDataException exc = assertThrows(SignalAppDataException.class,
                () -> userService.editCurrentUser(data.editUserDtoRequest(user, true)));
        assertEquals(SignalAppDataErrorCode.EMAIL_ALREADY_EXISTS, exc.getErrorCode());
    }

    @Test
    public void changePasswordCurrentUserOk() {
        User user = data.userWithConfirmedEmail();
        String token = createToken();
        fillSecurityContextHolder(user, token);
        ChangePasswordDtoRequest request = data.changePasswordDtoRequest();
        assertDoesNotThrow(() -> userService.changePasswordCurrentUser(request));
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertAll(
                () -> assertTrue(passwordEncoder.matches(request.getPassword(), capturedUser.getPassword())),
                () -> assertEquals(user.getId(), capturedUser.getId()),
                () -> assertEquals(user.getEmail(), capturedUser.getEmail()),
                () -> assertEquals(user.getFirstName(), capturedUser.getFirstName()),
                () -> assertEquals(user.getLastName(), capturedUser.getLastName()),
                () -> assertEquals(user.getPatronymic(), capturedUser.getPatronymic())
        );
    }

    @Test
    public void changePasswordCurrentUserWrongOldPasswordException() {
        User user = data.userWithConfirmedEmail();
        String token = createToken();
        fillSecurityContextHolder(user, token);
        ChangePasswordDtoRequest request = data.changePasswordDtoRequest()
                .setOldPassword(user.getPassword() + "_");
        SignalAppDataException exc = assertThrows(SignalAppDataException.class,
                () -> userService.changePasswordCurrentUser(request));
        assertEquals(SignalAppDataErrorCode.WRONG_OLD_PASSWORD, exc.getErrorCode());
    }

    @Test
    public void changePasswordCurrentUserUnauthorizedException() {
        ChangePasswordDtoRequest request = data.changePasswordDtoRequest();
        fillSecurityContextHolder();
        assertThrows(SignalAppUnauthorizedException.class,
                () -> userService.changePasswordCurrentUser(request));
    }

    @Test
    public void getCurrentUserInfoOk() throws SignalAppUnauthorizedException {
        User user = data.userWithConfirmedEmail();
        String token = createToken();
        fillSecurityContextHolder(user, token);
        UserDtoResponse response = userService.getCurrentUserInfo();
        assertAll(
                () -> assertEquals(user.getId(), response.getId()),
                () -> assertEquals(user.getEmail(), response.getEmail()),
                () -> assertEquals(user.getFirstName(), response.getFirstName()),
                () -> assertEquals(user.getLastName(), response.getLastName()),
                () -> assertEquals(user.getPatronymic(), response.getPatronymic()),
                () -> assertEquals(user.isEmailConfirmed(), response.isEmailConfirmed())
        );
    }

    @Test
    public void getCurrentUserInfoUnauthorizedException() {
        fillSecurityContextHolder();
        assertThrows(SignalAppUnauthorizedException.class,
                () -> userService.getCurrentUserInfo());
    }

    @Test
    public void makeUserEmailConfirmationOk() throws MessagingException {
        User user = data.userWithUnconfirmedEmail();
        String token = createToken();
        fillSecurityContextHolder(user, token);
        EmailConfirmDtoRequest request = data.emailConfirmDtoRequest();
        assertDoesNotThrow(() -> userService.makeUserEmailConfirmation(request));
        verify(userConfirmRepository).save(userConfirmCaptor.capture());
        verify(mailTransport).send(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());
        UserConfirm capturedUserConfirm = userConfirmCaptor.getValue();
        String capturedEmail = emailCaptor.getValue();
        String capturedSubject = subjectCaptor.getValue();
        String capturedBody = bodyCaptor.getValue();
        assertAll(
                () -> assertEquals(user.getId(), capturedUserConfirm.getId().getUser().getId()),
                () -> assertEquals(user.getEmail(), capturedEmail),
                () -> assertEquals(request.getLocaleTitle(), capturedSubject),
                () -> assertTrue(capturedBody.contains(capturedUserConfirm.getCode()))
        );
        // todo more assertions
    }

    private void prepareThrowRecaptchaTokenNotVerifiedException() {
        doThrow(new SignalAppException(SignalAppErrorCode.RECAPTCHA_TOKEN_NOT_VERIFIED, null))
                .when(recaptchaVerifier).verify(any(String.class));
    }

    private String createToken() {
        return String.valueOf(randomUUID());
    }

    private void fillSecurityContextHolder(User user, String token) {
        UserDetails userDetails = new SignalAppUserDetails(user).setToken(token);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext()
                .setAuthentication(usernamePasswordAuthenticationToken);
    }

    private void fillSecurityContextHolder() {
        SecurityContextHolder.getContext()
                .setAuthentication(UsernamePasswordAuthenticationToken.unauthenticated("", ""));
    }

    private static class Data {

        private static final Integer ID = 1;
        private static final String CAPTCHA_TOKEN = "captcha_token";
        private static final String EMAIL = "test@test";
        private static final String PASSWORD = "test@test";
        private static final String FIRST_NAME = "First";
        private static final String LAST_NAME = "Last";
        private static final String PATRONYMIC = "Patronymic";

        private final PasswordEncoder passwordEncoder = UserServiceTest.PASSWORD_ENCODER;

        private LoginDtoRequest loginDtoRequest() {
            return new LoginDtoRequest()
                    .setToken(CAPTCHA_TOKEN)
                    .setEmail(EMAIL)
                    .setPassword(PASSWORD);
        }

        private UserDtoRequest userDtoRequest() {
            return new UserDtoRequest()
                    .setToken(CAPTCHA_TOKEN)
                    .setEmail(EMAIL)
                    .setFirstName(FIRST_NAME)
                    .setLastName(LAST_NAME)
                    .setPatronymic(PATRONYMIC)
                    .setPassword(PASSWORD);
        }

        private EditUserDtoRequest editUserDtoRequest(User user, boolean modifyEmail) {
            final String modifier = "1";
            return new EditUserDtoRequest()
                    .setEmail(modifyEmail ? user.getEmail() + modifier : user.getEmail())
                    .setFirstName(user.getFirstName() + modifier)
                    .setLastName(user.getLastName() + modifier)
                    .setPatronymic(user.getPatronymic() + modifier);
        }

        private ChangePasswordDtoRequest changePasswordDtoRequest() {
            return new ChangePasswordDtoRequest()
                    .setPassword(PASSWORD + "1")
                    .setOldPassword(PASSWORD);
        }

        private EmailConfirmDtoRequest emailConfirmDtoRequest() {
            return new EmailConfirmDtoRequest()
                    .setOrigin("origin")
                    .setLocaleTitle("SignalApp - confirm email")
                    .setLocaleMsg("To confirm you email use the link $origin$/api/users/confirm/$code$");
        }

        private User user() {
            return new User()
                    .setId(ID)
                    .setEmail(EMAIL)
                    .setPassword(passwordEncoder.encode(PASSWORD))
                    .setFirstName(FIRST_NAME)
                    .setLastName(LAST_NAME)
                    .setPatronymic(PATRONYMIC);
        }

        private User userWithUnconfirmedEmail() {
            return user().setEmailConfirmed(false);
        }

        private User userWithConfirmedEmail() {
            return user().setEmailConfirmed(true);
        }
    }

}
