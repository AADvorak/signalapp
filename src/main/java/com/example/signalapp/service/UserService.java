package com.example.signalapp.service;

import com.example.signalapp.ApplicationProperties;
import com.example.signalapp.dto.request.ChangePasswordDtoRequest;
import com.example.signalapp.dto.request.EditUserDtoRequest;
import com.example.signalapp.dto.request.LoginDtoRequest;
import com.example.signalapp.dto.request.UserDtoRequest;
import com.example.signalapp.dto.response.ResponseWithToken;
import com.example.signalapp.dto.response.UserDtoResponse;
import com.example.signalapp.error.SignalAppDataErrorCode;
import com.example.signalapp.error.SignalAppDataException;
import com.example.signalapp.error.SignalAppUnauthorizedException;
import com.example.signalapp.mapper.UserMapper;
import com.example.signalapp.model.User;
import com.example.signalapp.model.UserPK;
import com.example.signalapp.model.UserToken;
import com.example.signalapp.repository.UserRepository;
import com.example.signalapp.repository.UserTokenRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.UUID.randomUUID;

@Service
public class UserService extends ServiceBase {

    private final UserRepository userRepository;

    public UserService(UserTokenRepository userTokenRepository, ApplicationProperties applicationProperties, UserRepository userRepository) {
        super(userTokenRepository, applicationProperties);
        this.userRepository = userRepository;
    }

    public ResponseWithToken<UserDtoResponse> register(UserDtoRequest request) throws SignalAppDataException {
        User user;
        try {
            user = userRepository.save(UserMapper.INSTANCE.dtoToUser(request));
        } catch (DataIntegrityViolationException ex) {
            throw new SignalAppDataException(SignalAppDataErrorCode.EMAIL_ALREADY_EXISTS, List.of("email"));
        }
        return new ResponseWithToken<>(UserMapper.INSTANCE.userToDto(user), generateAndSaveToken(user));
    }

    public ResponseWithToken<UserDtoResponse> login(LoginDtoRequest request) throws SignalAppDataException {
        User user = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());
        if (user == null) {
            throw new SignalAppDataException(SignalAppDataErrorCode.WRONG_EMAIL_PASSWORD, List.of("email", "password"));
        }
        return new ResponseWithToken<>(UserMapper.INSTANCE.userToDto(user), generateAndSaveToken(user));
    }

    @Transactional
    public void logout(String token) throws SignalAppUnauthorizedException {
        if (userTokenRepository.deleteByToken(token) == 0) {
            throw new SignalAppUnauthorizedException();
        }
    }

    @Transactional
    public void deleteCurrentUser(String token) throws SignalAppUnauthorizedException {
        if (userRepository.deleteByToken(token) == 0) {
            throw new SignalAppUnauthorizedException();
        }
    }

    public UserDtoResponse editCurrentUser(String token, EditUserDtoRequest request) throws SignalAppUnauthorizedException,
            SignalAppDataException {
        User user = getUserByToken(token);
        if (!Objects.equals(user.getEmail(), request.getEmail())) {
            user.setEmailConfirmed(false);
        }
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPatronymic(request.getPatronymic());
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new SignalAppDataException(SignalAppDataErrorCode.EMAIL_ALREADY_EXISTS, List.of("email"));
        }
        return UserMapper.INSTANCE.userToDto(user);
    }

    public void changePasswordCurrentUser(String token, ChangePasswordDtoRequest request)
            throws SignalAppUnauthorizedException, SignalAppDataException {
        User user = getUserByToken(token);
        if (!Objects.equals(user.getPassword(), request.getOldPassword())) {
            throw new SignalAppDataException(SignalAppDataErrorCode.WRONG_OLD_PASSWORD, List.of("oldPassword"));
        }
        user.setPassword(request.getPassword());
        userRepository.save(user);
    }

    public UserDtoResponse getCurrentUserInfo(String token) throws SignalAppUnauthorizedException {
        return UserMapper.INSTANCE.userToDto(getUserByToken(token));
    }

    private String generateAndSaveToken(User user) {
        String token = String.valueOf(randomUUID());
        Optional<UserToken> optionalUserToken = userTokenRepository.findById(new UserPK(user));
        if (optionalUserToken.isPresent()) {
            UserToken userToken = optionalUserToken.get();
            userToken.setToken(token);
            userToken.setLastActionTime(LocalDateTime.now());
            userTokenRepository.save(userToken);
        } else {
            userTokenRepository.save(new UserToken(new UserPK(user), token, LocalDateTime.now()));
        }
        return token;
    }

}
