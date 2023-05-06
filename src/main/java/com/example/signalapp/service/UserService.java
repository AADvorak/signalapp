package com.example.signalapp.service;

import com.example.signalapp.ApplicationProperties;
import com.example.signalapp.dto.request.*;
import com.example.signalapp.dto.response.ResponseWithToken;
import com.example.signalapp.dto.response.UserDtoResponse;
import com.example.signalapp.error.SignalAppDataErrorCode;
import com.example.signalapp.error.SignalAppDataException;
import com.example.signalapp.error.SignalAppUnauthorizedException;
import com.example.signalapp.file.FileManager;
import com.example.signalapp.mail.MailTransport;
import com.example.signalapp.mapper.UserMapper;
import com.example.signalapp.model.User;
import com.example.signalapp.model.UserConfirm;
import com.example.signalapp.model.UserPK;
import com.example.signalapp.model.UserToken;
import com.example.signalapp.repository.UserConfirmRepository;
import com.example.signalapp.repository.UserRepository;
import com.example.signalapp.repository.UserTokenRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static java.util.UUID.randomUUID;

@Service
public class UserService extends ServiceBase {

    public static final int MAX_PASSWORD_LENGTH = 72;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A,
            new SecureRandom("OjKotPaniMatko".getBytes()));

    public static final String EMAIL_CONFIRM_OK = "email-confirm-ok";
    public static final String EMAIL_CONFIRM_ERROR = "email-confirm-error";

    private final UserRepository userRepository;

    private final UserConfirmRepository userConfirmRepository;

    private final MailTransport mailTransport;

    private final FileManager fileManager;

    public UserService(UserTokenRepository userTokenRepository, ApplicationProperties applicationProperties,
                       UserRepository userRepository, UserConfirmRepository userConfirmRepository,
                       MailTransport mailTransport, FileManager fileManager) {
        super(userTokenRepository, applicationProperties);
        this.userRepository = userRepository;
        this.userConfirmRepository = userConfirmRepository;
        this.mailTransport = mailTransport;
        this.fileManager = fileManager;
    }

    public ResponseWithToken<UserDtoResponse> register(UserDtoRequest request) throws SignalAppDataException {
        User user = UserMapper.INSTANCE.dtoToUser(request);
        user.setPassword(encoder.encode(user.getPassword()));
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new SignalAppDataException(SignalAppDataErrorCode.EMAIL_ALREADY_EXISTS);
        }
        return new ResponseWithToken<UserDtoResponse>()
                .setResponse(UserMapper.INSTANCE.userToDto(user))
                .setToken(generateAndSaveToken(user));
    }

    public ResponseWithToken<UserDtoResponse> login(LoginDtoRequest request) throws SignalAppDataException {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new SignalAppDataException(SignalAppDataErrorCode.WRONG_EMAIL_PASSWORD);
        }
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new SignalAppDataException(SignalAppDataErrorCode.WRONG_EMAIL_PASSWORD);
        }
        return new ResponseWithToken<UserDtoResponse>()
                .setResponse(UserMapper.INSTANCE.userToDto(user))
                .setToken(generateAndSaveToken(user));
    }

    @Transactional
    public void logout(String token) throws SignalAppUnauthorizedException {
        if (userTokenRepository.deleteByToken(token) == 0) {
            throw new SignalAppUnauthorizedException();
        }
    }

    @Transactional
    public void deleteCurrentUser(String token) throws SignalAppUnauthorizedException {
        int userId = getUserByToken(token).getId();
        if (userRepository.deleteByToken(token) == 0) {
            throw new SignalAppUnauthorizedException();
        } else {
            fileManager.deleteAllUserData(userId);
        }
    }

    public UserDtoResponse editCurrentUser(String token, EditUserDtoRequest request) throws SignalAppUnauthorizedException,
            SignalAppDataException {
        User user = getUserByToken(token);
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

    public void changePasswordCurrentUser(String token, ChangePasswordDtoRequest request)
            throws SignalAppUnauthorizedException, SignalAppDataException {
        User user = getUserByToken(token);
        if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new SignalAppDataException(SignalAppDataErrorCode.WRONG_OLD_PASSWORD);
        }
        user.setPassword(encoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    public UserDtoResponse getCurrentUserInfo(String token) throws SignalAppUnauthorizedException {
        return UserMapper.INSTANCE.userToDto(getUserByToken(token));
    }

    @Transactional(rollbackFor = MessagingException.class)
    public void makeUserEmailConfirmation(String token, EmailConfirmDtoRequest request) throws SignalAppUnauthorizedException,
            MessagingException, SignalAppDataException {
        User user = getUserByToken(token);
        if (user.isEmailConfirmed()) {
            throw new SignalAppDataException(SignalAppDataErrorCode.EMAIL_ALREADY_CONFIRMED);
        }
        UserConfirm userConfirm = new UserConfirm()
                .setId(new UserPK().setUser(user))
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
        if (userConfirm == null || userRepository.updateSetEmailConfirmedTrue(userConfirm.getId().getUser().getId()) == 0) {
            return EMAIL_CONFIRM_ERROR;
        }
        userConfirmRepository.delete(userConfirm);
        return EMAIL_CONFIRM_OK;
    }

    @Transactional(rollbackFor = MessagingException.class)
    public void restorePassword(RestorePasswordDtoRequest request) throws SignalAppDataException, MessagingException {
        User user = userRepository.findByEmailAndEmailConfirmedTrue(request.getEmail());
        if (user == null) {
            throw new SignalAppDataException(SignalAppDataErrorCode.WRONG_EMAIL);
        }
        String newPassword = RandomStringUtils.randomAlphanumeric(applicationProperties.getMinPasswordLength());
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        mailTransport.send(user.getEmail(), request.getLocaleTitle(), request.getLocaleMsg()
                .replace("$newPassword$", newPassword));
    }

    private String generateAndSaveToken(User user) {
        String token = String.valueOf(randomUUID());
        UserPK userPK = new UserPK().setUser(user);
        Optional<UserToken> optionalUserToken = userTokenRepository.findById(userPK);
        if (optionalUserToken.isPresent()) {
            userTokenRepository.save(optionalUserToken.get()
                    .setToken(token)
                    .setLastActionTime(LocalDateTime.now()));
        } else {
            userTokenRepository.save(new UserToken()
                    .setId(userPK)
                    .setToken(token)
                    .setLastActionTime(LocalDateTime.now()));
        }
        return token;
    }

}
