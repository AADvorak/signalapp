package com.example.signalapp.service;

import com.example.signalapp.ApplicationProperties;
import com.example.signalapp.error.SignalAppUnauthorizedException;
import com.example.signalapp.model.User;
import com.example.signalapp.model.UserToken;
import com.example.signalapp.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ServiceBase {

    protected final UserTokenRepository userTokenRepository;

    protected final ApplicationProperties applicationProperties;

    protected User getUserByToken(String token) throws SignalAppUnauthorizedException {
        UserToken userToken = findActiveToken(token);
        if (userToken == null) {
            throw new SignalAppUnauthorizedException();
        } else {
            userToken.setLastActionTime(LocalDateTime.now());
            userTokenRepository.save(userToken);
        }
        return userToken.getId().getUser();
    }

    protected UserToken findActiveToken(String token) {
        return userTokenRepository.findActiveToken(token, LocalDateTime.now(), applicationProperties.getUserIdleTimeout());
    }

}
