package link.signalapp.service;

import link.signalapp.ApplicationProperties;
import link.signalapp.error.SignalAppUnauthorizedException;
import link.signalapp.model.User;
import link.signalapp.model.UserToken;
import link.signalapp.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ServiceBase {

    protected final UserTokenRepository userTokenRepository;

    protected final ApplicationProperties applicationProperties;

    protected User getUserByToken(String token) throws SignalAppUnauthorizedException {
        UserToken userToken = userTokenRepository.findActiveToken(token, LocalDateTime.now(),
                applicationProperties.getUserIdleTimeout());
        if (userToken == null) {
            throw new SignalAppUnauthorizedException();
        } else {
            userToken.setLastActionTime(LocalDateTime.now());
            userTokenRepository.save(userToken);
        }
        return userToken.getId().getUser();
    }

}
