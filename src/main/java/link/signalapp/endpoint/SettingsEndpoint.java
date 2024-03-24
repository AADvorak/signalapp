package link.signalapp.endpoint;

import link.signalapp.properties.ApplicationProperties;
import link.signalapp.dto.response.SettingsDtoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsEndpoint {

    private final ApplicationProperties applicationProperties;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public SettingsDtoResponse get() {
        return new SettingsDtoResponse().setVerifyCaptcha(applicationProperties.isVerifyCaptcha());
    }

}
