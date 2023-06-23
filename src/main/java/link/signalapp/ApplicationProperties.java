package link.signalapp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "application")
@Component
@Data
public class ApplicationProperties {

    private int maxNameLength;

    private int minPasswordLength;

    private int userIdleTimeout;

    private boolean debug;

    private boolean verifyCaptcha;

    private String dataPath;

}
