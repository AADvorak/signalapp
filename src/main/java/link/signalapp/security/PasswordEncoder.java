package link.signalapp.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordEncoder extends BCryptPasswordEncoder {

    public PasswordEncoder() {
        super(BCryptPasswordEncoder.BCryptVersion.$2A, new SecureRandom("OjKotPaniMatko".getBytes()));
    }

}
