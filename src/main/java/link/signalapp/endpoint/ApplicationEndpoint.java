package link.signalapp.endpoint;

import link.signalapp.properties.ApplicationProperties;
import link.signalapp.dto.response.SettingsDtoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationEndpoint {

    private final ApplicationProperties applicationProperties;

    @GetMapping(path = "/settings", produces = MediaType.APPLICATION_JSON_VALUE)
    public SettingsDtoResponse get() {
        return new SettingsDtoResponse().setVerifyCaptcha(applicationProperties.isVerifyCaptcha());
    }

    @GetMapping(path = "/version", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getAppVersion() {
        // todo keep this value in one place
        return "1.6.0-beta1";
    }

}
