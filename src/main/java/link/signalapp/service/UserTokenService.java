package link.signalapp.service;

import link.signalapp.properties.ApplicationProperties;
import link.signalapp.model.UserToken;
import link.signalapp.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserTokenService {

    private final UserTokenRepository userTokenRepository;
    private final ApplicationProperties applicationProperties;

    public UserToken getActiveTokenAndRefreshLastActionTime(String token) {
        UserToken userToken = userTokenRepository.findActiveToken(token, LocalDateTime.now(),
                applicationProperties.getUserIdleTimeout());
        if (userToken == null) {
            return null;
        } else {
            userToken.setLastActionTime(LocalDateTime.now());
            userTokenRepository.save(userToken);
        }
        return userToken;
    }
}
