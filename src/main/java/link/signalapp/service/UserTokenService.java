package link.signalapp.service;

import link.signalapp.ApplicationProperties;
import link.signalapp.error.SignalAppUnauthorizedException;
import link.signalapp.model.User;
import link.signalapp.model.UserToken;
import link.signalapp.repository.UserRepository;
import link.signalapp.repository.UserTokenRepository;
import link.signalapp.security.SignalAppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserTokenService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final ApplicationProperties applicationProperties;

    public User getUserByToken(String token) throws SignalAppUnauthorizedException {
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new SignalAppUserDetails(user);
    }
}
