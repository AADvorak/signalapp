package link.signalapp.security;

import link.signalapp.ApplicationProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordEncoder extends BCryptPasswordEncoder {

    public PasswordEncoder(ApplicationProperties properties) {
        super(BCryptPasswordEncoder.BCryptVersion.$2A, new SecureRandom(properties.getSecureRandomSeed().getBytes()));
    }

}
