package link.signalapp;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "application")
@Component
@Data
@Accessors(chain = true)
public class ApplicationProperties {

    private int maxNameLength;

    private int minPasswordLength;

    private int userIdleTimeout;

    private boolean debug;

    private boolean verifyCaptcha;

    private String dataPath;

    private String secureRandomSeed;

}
