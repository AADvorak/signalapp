package link.signalapp.service;

import link.signalapp.properties.ApplicationProperties;
import link.signalapp.captcha.RecaptchaVerifier;
import link.signalapp.dto.request.*;
import link.signalapp.dto.response.ResponseWithToken;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.error.code.SignalAppDataErrorCode;
import link.signalapp.error.exception.SignalAppDataException;
import link.signalapp.error.exception.SignalAppUnauthorizedException;
import link.signalapp.file.FileManager;
import link.signalapp.mail.MailTransport;
import link.signalapp.mapper.UserMapper;
import link.signalapp.model.*;
import link.signalapp.repository.UserConfirmRepository;
import link.signalapp.repository.UserRepository;
import link.signalapp.repository.UserTokenRepository;
import link.signalapp.security.PasswordEncoder;
import link.signalapp.security.SignalAppUserDetails;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class UserService extends ServiceBase {

    public static final int MAX_PASSWORD_LENGTH = 72;

    public static final String EMAIL_CONFIRM_OK = "email-confirm-ok";
    public static final String EMAIL_CONFIRM_ERROR = "email-confirm-error";

    private final UserRepository userRepository;
    private final UserConfirmRepository userConfirmRepository;
    private final MailTransport mailTransport;
    private final FileManager fileManager;
    private final RecaptchaVerifier recaptchaVerifier;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationProperties applicationProperties;
    private final UserTokenRepository userTokenRepository;

    public ResponseWithToken<UserDtoResponse> register(UserDtoRequest request) {
        if (applicationProperties.isVerifyCaptcha()) {
            recaptchaVerifier.verify(request.getToken());
        }
        User user = UserMapper.INSTANCE.dtoToUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new SignalAppDataException(SignalAppDataErrorCode.EMAIL_ALREADY_EXISTS);
        }
        return new ResponseWithToken<UserDtoResponse>()
                .setResponse(UserMapper.INSTANCE.userToDto(user))
                .setToken(generateAndSaveToken(user));
    }

    @Transactional
    public ResponseWithToken<UserDtoResponse> login(LoginDtoRequest request) {
        if (applicationProperties.isVerifyCaptcha()) {
            recaptchaVerifier.verify(request.getToken());
        }
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new SignalAppDataException(SignalAppDataErrorCode.WRONG_EMAIL_PASSWORD);
        }
        userTokenRepository.deleteOldTokens(user.getId(), LocalDateTime.now(),
                applicationProperties.getUserIdleTimeout());
        return new ResponseWithToken<UserDtoResponse>()
                .setResponse(UserMapper.INSTANCE.userToDto(user))
                .setToken(generateAndSaveToken(user));
    }

    @Transactional
    public void logout() {
        if (userTokenRepository.deleteByToken(getTokenFromContext()) == 0) {
            throw new SignalAppUnauthorizedException();
        }
        SecurityContextHolder.clearContext();
    }

    @Transactional
    public void deleteCurrentUser() {
        SignalAppUserDetails userDetails = getUserDetailsFromContext();
        if (userRepository.deleteByToken(userDetails.getToken()) == 0) {
            throw new SignalAppUnauthorizedException();
        } else {
            fileManager.deleteAllUserData(userDetails.getUser().getId());
        }
    }

    public UserDtoResponse editCurrentUser(EditUserDtoRequest request) {
        User user = getUserFromContext();
        if (!Objects.equals(user.getEmail(), request.getEmail())) {
            user.setEmailConfirmed(false);
        }
        user.setEmail(request.getEmail())
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setPatronymic(request.getPatronymic());
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new SignalAppDataException(SignalAppDataErrorCode.EMAIL_ALREADY_EXISTS);
        }
        return UserMapper.INSTANCE.userToDto(user);
    }

    public void changePasswordCurrentUser(ChangePasswordDtoRequest request) {
        User user = getUserFromContext();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new SignalAppDataException(SignalAppDataErrorCode.WRONG_OLD_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    public UserDtoResponse getCurrentUserInfo() {
        return UserMapper.INSTANCE.userToDto(getUserFromContext());
    }

    @Transactional(rollbackFor = MessagingException.class)
    public void makeUserEmailConfirmation(EmailConfirmDtoRequest request) throws MessagingException {
        User user = getUserFromContext();
        if (user.isEmailConfirmed()) {
            throw new SignalAppDataException(SignalAppDataErrorCode.EMAIL_ALREADY_CONFIRMED);
        }
        UserConfirm userConfirm = new UserConfirm()
                .setId(new UserConfirmPK().setUser(user))
                .setCode(String.valueOf(randomUUID()))
                .setCreateTime(LocalDateTime.now());
        userConfirmRepository.save(userConfirm);
        mailTransport.send(user.getEmail(), request.getLocaleTitle(), request.getLocaleMsg()
                        .replace("$origin$", request.getOrigin())
                        .replace("$code$", userConfirm.getCode()));
    }

    @Transactional
    public String confirmEmail(String code) {
        UserConfirm userConfirm = userConfirmRepository.findByCode(code);
        if (userConfirm == null
                || userRepository.updateSetEmailConfirmedTrue(userConfirm.getId().getUser().getId()) == 0) {
            return EMAIL_CONFIRM_ERROR;
        }
        userConfirmRepository.delete(userConfirm);
        return EMAIL_CONFIRM_OK;
    }

    @Transactional(rollbackFor = MessagingException.class)
    public void restorePassword(RestorePasswordDtoRequest request) throws MessagingException {
        User user = userRepository.findByEmailAndEmailConfirmedTrue(request.getEmail());
        if (user == null) {
            throw new SignalAppDataException(SignalAppDataErrorCode.WRONG_EMAIL);
        }
        String newPassword = RandomStringUtils.randomAlphanumeric(applicationProperties.getMinPasswordLength());
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        mailTransport.send(user.getEmail(), request.getLocaleTitle(), request.getLocaleMsg()
                .replace("$newPassword$", newPassword));
    }

    private String generateAndSaveToken(User user) {
        String token = String.valueOf(randomUUID());
        userTokenRepository.save(new UserToken()
                .setId(new UserTokenPK().setUser(user).setToken(token))
                .setLastActionTime(LocalDateTime.now()));
        return token;
    }

}
