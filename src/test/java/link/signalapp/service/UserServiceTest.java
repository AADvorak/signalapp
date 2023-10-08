// todo mock spring security
//package link.signalapp.service;
//
//import link.signalapp.ApplicationProperties;
//import link.signalapp.captcha.RecaptchaVerifier;
//import link.signalapp.dto.request.EditUserDtoRequest;
//import link.signalapp.dto.request.LoginDtoRequest;
//import link.signalapp.dto.request.UserDtoRequest;
//import link.signalapp.dto.response.ResponseWithToken;
//import link.signalapp.dto.response.UserDtoResponse;
//import link.signalapp.error.SignalAppDataErrorCode;
//import link.signalapp.error.SignalAppDataException;
//import link.signalapp.error.SignalAppUnauthorizedException;
//import link.signalapp.file.FileManager;
//import link.signalapp.mail.MailTransport;
//import link.signalapp.model.User;
//import link.signalapp.model.UserTokenPK;
//import link.signalapp.model.UserToken;
//import link.signalapp.repository.UserConfirmRepository;
//import link.signalapp.repository.UserRepository;
//import link.signalapp.repository.UserTokenRepository;
//import link.signalapp.security.PasswordEncoder;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.dao.DataIntegrityViolationException;
//
//import static java.util.UUID.randomUUID;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.AdditionalAnswers.returnsFirstArg;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class UserServiceTest {
//
//    private final Data data = new Data();
//
//    @InjectMocks
//    private UserService userService;
//
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private UserConfirmRepository userConfirmRepository;
//    @Mock
//    private MailTransport mailTransport;
//    @Mock
//    private FileManager fileManager;
//    @Mock
//    private RecaptchaVerifier recaptchaVerifier;
//    @Mock
//    private UserTokenRepository userTokenRepository;
//    @Mock
//    private ApplicationProperties applicationProperties;
//    @Spy
//    private final PasswordEncoder passwordEncoder = new PasswordEncoder();
//
//    @Captor
//    private ArgumentCaptor<User> userCaptor;
//    @Captor
//    private ArgumentCaptor<String> tokenCaptor;
//    @Captor
//    private ArgumentCaptor<UserToken> userTokenCaptor;
//    @Captor
//    private ArgumentCaptor<String> rawPasswordCaptor;
//    @Captor
//    private ArgumentCaptor<Integer> idCaptor;
//
//    @Test
//    public void registerOk() throws Exception {
//        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
//        when(userRepository.save(any(User.class))).then(returnsFirstArg());
//        UserDtoRequest request = data.userDtoRequest();
//        ResponseWithToken<UserDtoResponse> response = userService.register(request);
//        verify(userRepository).save(userCaptor.capture());
//        verify(recaptchaVerifier).verify(tokenCaptor.capture());
//        verify(userTokenRepository).save(userTokenCaptor.capture());
//        verify(passwordEncoder).encode(rawPasswordCaptor.capture());
//        User capturedUser = userCaptor.getValue();
//        String capturedToken = tokenCaptor.getValue();
//        UserToken capturedUserToken = userTokenCaptor.getValue();
//        String rawPassword = rawPasswordCaptor.getValue();
//        assertAll(
//                () -> assertEquals(capturedUserToken.getId().getToken(), response.getToken()),
//                () -> assertEquals(request.getEmail(), response.getResponse().getEmail()),
//                () -> assertEquals(request.getFirstName(), response.getResponse().getFirstName()),
//                () -> assertEquals(request.getLastName(), response.getResponse().getLastName()),
//                () -> assertEquals(request.getPatronymic(), response.getResponse().getPatronymic()),
//                () -> assertEquals(request.getEmail(), capturedUser.getEmail()),
//                () -> assertEquals(request.getPassword(), rawPassword),
//                () -> assertNotNull(capturedUser.getPassword()),
//                () -> assertEquals(request.getFirstName(), capturedUser.getFirstName()),
//                () -> assertEquals(request.getLastName(), capturedUser.getLastName()),
//                () -> assertEquals(request.getPatronymic(), capturedUser.getPatronymic()),
//                () -> assertFalse(capturedUser.isEmailConfirmed()),
//                () -> assertEquals(request.getToken(), capturedToken)
//        );
//    }
//
//    @Test
//    public void registerTokenNotVerifiedException() throws Exception {
//        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
//        prepareThrowRecaptchaTokenNotVerifiedException();
//        SignalAppDataException exc = assertThrows(SignalAppDataException.class,
//                () -> userService.register(data.userDtoRequest()));
//        assertEquals(SignalAppDataErrorCode.RECAPTCHA_TOKEN_NOT_VERIFIED, exc.getErrorCode());
//    }
//
//    @Test
//    public void registerEmailAlreadyExistsException() {
//        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
//        doThrow(new DataIntegrityViolationException(""))
//                .when(userRepository).save(any(User.class));
//        SignalAppDataException exc = assertThrows(SignalAppDataException.class,
//                () -> userService.register(data.userDtoRequest()));
//        assertEquals(SignalAppDataErrorCode.EMAIL_ALREADY_EXISTS, exc.getErrorCode());
//    }
//
//    @Test
//    public void loginOk() throws Exception {
//        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
//        LoginDtoRequest request = data.loginDtoRequest();
//        User user = data.userWithUnconfirmedEmail();
//        when(userRepository.findByEmail(Data.EMAIL)).thenReturn(user);
//        ResponseWithToken<UserDtoResponse> response = userService.login(request);
//        verify(recaptchaVerifier).verify(tokenCaptor.capture());
//        verify(userTokenRepository).save(userTokenCaptor.capture());
//        String capturedToken = tokenCaptor.getValue();
//        UserToken capturedUserToken = userTokenCaptor.getValue();
//        assertAll(
//                () -> assertEquals(capturedUserToken.getId().getToken(), response.getToken()),
//                () -> assertEquals(user.getEmail(), response.getResponse().getEmail()),
//                () -> assertEquals(user.isEmailConfirmed(), response.getResponse().isEmailConfirmed()),
//                () -> assertEquals(user.getFirstName(), response.getResponse().getFirstName()),
//                () -> assertEquals(user.getLastName(), response.getResponse().getLastName()),
//                () -> assertEquals(user.getPatronymic(), response.getResponse().getPatronymic()),
//                () -> assertEquals(request.getToken(), capturedToken)
//        );
//    }
//
//    @Test
//    public void loginTokenNotVerifiedException() throws Exception {
//        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
//        prepareThrowRecaptchaTokenNotVerifiedException();
//        SignalAppDataException exc = assertThrows(SignalAppDataException.class,
//                () -> userService.login(data.loginDtoRequest()));
//        assertEquals(SignalAppDataErrorCode.RECAPTCHA_TOKEN_NOT_VERIFIED, exc.getErrorCode());
//    }
//
//    @Test
//    public void loginUserIsNull() {
//        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
//        when(userRepository.findByEmail(Data.EMAIL)).thenReturn(null);
//        SignalAppDataException exc = assertThrows(SignalAppDataException.class,
//                () -> userService.login(data.loginDtoRequest()));
//        assertEquals(SignalAppDataErrorCode.WRONG_EMAIL_PASSWORD, exc.getErrorCode());
//    }
//
//    @Test
//    public void loginPasswordNotMatches() {
//        when(applicationProperties.isVerifyCaptcha()).thenReturn(true);
//        when(userRepository.findByEmail(Data.EMAIL)).thenReturn(data.userWithUnconfirmedEmail());
//        SignalAppDataException exc = assertThrows(SignalAppDataException.class,
//                () -> userService.login(data.loginDtoRequest().setPassword("wrong")));
//        assertEquals(SignalAppDataErrorCode.WRONG_EMAIL_PASSWORD, exc.getErrorCode());
//    }
//
//    @Test
//    public void logoutOk() {
//        String token = createToken();
//        when(userTokenRepository.deleteByToken(token)).thenReturn(1);
//        assertDoesNotThrow(() -> userService.logout());
//    }
//
//    @Test
//    public void logoutUnauthorizedException() {
//        String token = createToken();
//        when(userTokenRepository.deleteByToken(token)).thenReturn(0);
//        assertThrows(SignalAppUnauthorizedException.class, () -> userService.logout());
//    }
//
//    @Test
//    public void deleteUserOk() throws SignalAppUnauthorizedException {
//        String token = createToken();
//        User user = data.userWithConfirmedEmail();
//        when(userTokenRepository.findActiveToken(eq(token), any(), anyInt())).thenReturn(data.userToken(user, token));
//        when(userRepository.deleteByToken(token)).thenReturn(1);
//        userService.deleteCurrentUser();
//        verify(fileManager).deleteAllUserData(idCaptor.capture());
//        assertEquals(Data.ID, idCaptor.getValue());
//    }
//
//    @Test
//    public void deleteUserUnauthorizedException() {
//        String token = createToken();
//        assertThrows(SignalAppUnauthorizedException.class, () -> userService.logout());
//    }
//
//    @Test
//    public void editCurrentUserWithoutEmailChangeOk() throws SignalAppUnauthorizedException, SignalAppDataException {
//        User user = data.userWithConfirmedEmail();
//        EditUserDtoRequest request = data.editUserDtoRequest(user, false);
//        String token = createToken();
//        when(userTokenRepository.findActiveToken(eq(token), any(), anyInt())).thenReturn(data.userToken(user, token));
//        when(userRepository.save(any(User.class))).then(returnsFirstArg());
//        UserDtoResponse response = userService.editCurrentUser(request);
//        verify(userRepository).save(userCaptor.capture());
//        User capturedUser = userCaptor.getValue();
//        assertAll(
//                () -> assertEquals(user.getId(), capturedUser.getId()),
//                () -> assertEquals(user.getPassword(), capturedUser.getPassword()),
//                () -> assertEquals(request.getEmail(), capturedUser.getEmail()),
//                () -> assertEquals(request.getFirstName(), capturedUser.getFirstName()),
//                () -> assertEquals(request.getLastName(), capturedUser.getLastName()),
//                () -> assertEquals(request.getPatronymic(), capturedUser.getPatronymic()),
//                () -> assertTrue(capturedUser.isEmailConfirmed()),
//                () -> assertEquals(user.getId(), response.getId()),
//                () -> assertEquals(user.getEmail(), response.getEmail()),
//                () -> assertEquals(user.getFirstName(), response.getFirstName()),
//                () -> assertEquals(user.getLastName(), response.getLastName()),
//                () -> assertEquals(user.getPatronymic(), response.getPatronymic()),
//                () -> assertTrue(response.isEmailConfirmed())
//        );
//    }
//
//    @Test
//    public void editCurrentUserWithEmailChangeOk() throws SignalAppUnauthorizedException, SignalAppDataException {
//        User user = data.userWithConfirmedEmail();
//        EditUserDtoRequest request = data.editUserDtoRequest(user, true);
//        String token = createToken();
//        when(userTokenRepository.findActiveToken(eq(token), any(), anyInt())).thenReturn(data.userToken(user, token));
//        when(userRepository.save(any(User.class))).then(returnsFirstArg());
//        UserDtoResponse response = userService.editCurrentUser(request);
//        verify(userRepository).save(userCaptor.capture());
//        User capturedUser = userCaptor.getValue();
//        assertAll(
//                () -> assertEquals(user.getId(), capturedUser.getId()),
//                () -> assertEquals(user.getPassword(), capturedUser.getPassword()),
//                () -> assertEquals(request.getEmail(), capturedUser.getEmail()),
//                () -> assertEquals(request.getFirstName(), capturedUser.getFirstName()),
//                () -> assertEquals(request.getLastName(), capturedUser.getLastName()),
//                () -> assertEquals(request.getPatronymic(), capturedUser.getPatronymic()),
//                () -> assertFalse(capturedUser.isEmailConfirmed()),
//                () -> assertEquals(user.getId(), response.getId()),
//                () -> assertEquals(user.getEmail(), response.getEmail()),
//                () -> assertEquals(user.getFirstName(), response.getFirstName()),
//                () -> assertEquals(user.getLastName(), response.getLastName()),
//                () -> assertEquals(user.getPatronymic(), response.getPatronymic()),
//                () -> assertFalse(response.isEmailConfirmed())
//        );
//    }
//
//    @Test
//    public void editCurrentUserUnauthorizedException() {
//        String token = createToken();
//        assertThrows(SignalAppUnauthorizedException.class,
//                () -> userService.editCurrentUser(data.editUserDtoRequest(data.userWithConfirmedEmail(), false)));
//    }
//
//    @Test
//    public void editCurrentUserEmailAlreadyExistsException() {
//        String token = createToken();
//        User user = data.userWithConfirmedEmail();
//        when(userTokenRepository.findActiveToken(eq(token), any(), anyInt())).thenReturn(data.userToken(user, token));
//        doThrow(new DataIntegrityViolationException(""))
//                .when(userRepository).save(any(User.class));
//        SignalAppDataException exc = assertThrows(SignalAppDataException.class,
//                () -> userService.editCurrentUser(data.editUserDtoRequest(user, true)));
//        assertEquals(SignalAppDataErrorCode.EMAIL_ALREADY_EXISTS, exc.getErrorCode());
//    }
//
//    private void prepareThrowRecaptchaTokenNotVerifiedException() throws Exception {
//        doThrow(new SignalAppDataException(SignalAppDataErrorCode.RECAPTCHA_TOKEN_NOT_VERIFIED))
//                .when(recaptchaVerifier).verify(any(String.class));
//    }
//
//    private String createToken() {
//        return String.valueOf(randomUUID());
//    }
//
//    private static class Data {
//
//        private static final Integer ID = 1;
//        private static final String TOKEN = "token";
//        private static final String EMAIL = "test@test";
//        private static final String PASSWORD = "test@test";
//        private static final String FIRST_NAME = "First";
//        private static final String LAST_NAME = "Last";
//        private static final String PATRONYMIC = "Patronymic";
//
//        private final PasswordEncoder passwordEncoder = new PasswordEncoder();
//
//        private LoginDtoRequest loginDtoRequest() {
//            return new LoginDtoRequest()
//                    .setToken(TOKEN)
//                    .setEmail(EMAIL)
//                    .setPassword(PASSWORD);
//        }
//
//        private UserDtoRequest userDtoRequest() {
//            return new UserDtoRequest()
//                    .setToken(TOKEN)
//                    .setEmail(EMAIL)
//                    .setFirstName(FIRST_NAME)
//                    .setLastName(LAST_NAME)
//                    .setPatronymic(PATRONYMIC)
//                    .setPassword(PASSWORD);
//        }
//
//        private EditUserDtoRequest editUserDtoRequest(User user, boolean modifyEmail) {
//            final String modifier = "1";
//            return new EditUserDtoRequest()
//                    .setEmail(modifyEmail ? user.getEmail() + modifier : user.getEmail())
//                    .setFirstName(user.getFirstName() + modifier)
//                    .setLastName(user.getLastName() + modifier)
//                    .setPatronymic(user.getPatronymic() + modifier);
//        }
//
//        private User user() {
//            return new User()
//                    .setId(ID)
//                    .setEmail(EMAIL)
//                    .setPassword(passwordEncoder.encode(PASSWORD))
//                    .setFirstName(FIRST_NAME)
//                    .setLastName(LAST_NAME)
//                    .setPatronymic(PATRONYMIC);
//        }
//
//        private User userWithUnconfirmedEmail() {
//            return user().setEmailConfirmed(false);
//        }
//
//        private User userWithConfirmedEmail() {
//            return user().setEmailConfirmed(true);
//        }
//
//        private UserToken userToken(User user, String token) {
//            return new UserToken()
//                    .setId(new UserTokenPK().setUser(user).setToken(token));
//        }
//    }
//
//}
