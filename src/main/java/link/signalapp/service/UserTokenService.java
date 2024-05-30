package link.signalapp.service;

import link.signalapp.properties.ApplicationProperties;
import link.signalapp.model.UserToken;
import link.signalapp.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserTokenService {

    private final UserTokenRepository userTokenRepository;
    private final ApplicationProperties applicationProperties;

    @Transactional
    public UserToken getActiveTokenAndRefreshLastActionTime(String token) {
        UserToken userToken = userTokenRepository.findActiveToken(token, LocalDateTime.now(),
                applicationProperties.getUserIdleTimeout());
        if (userToken == null) {
            return null;
        }
        userTokenRepository.updateLastActionTimeByToken(LocalDateTime.now(), token);
        return userToken;
    }
}
