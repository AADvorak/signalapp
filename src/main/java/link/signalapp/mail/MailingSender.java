package link.signalapp.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "mailing")
@Component
@Data
public class MailingSender {

    private String smtpHost;

    private String smtpPort;

    private String username;

    private String password;

}
