package link.signalapp.captcha;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "recaptcha")
@Component
@Data
public class RecaptchaParams {

    private String url;

    private String secret;

}
