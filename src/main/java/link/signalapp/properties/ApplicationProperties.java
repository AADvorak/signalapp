package link.signalapp.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "application")
@Component
@Getter
@Setter
@Accessors(chain = true)
public class ApplicationProperties {

    private int maxPageSize;

    private int maxNameLength;

    private int minPasswordLength;

    private int userIdleTimeout;

    private boolean verifyCaptcha;

    private String dataPath;

    private String secureRandomSeed;

    private ApplicationLimits limits;

}
