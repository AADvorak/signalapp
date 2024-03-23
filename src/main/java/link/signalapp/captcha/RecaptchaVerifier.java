package link.signalapp.captcha;

import link.signalapp.error.code.SignalAppErrorCode;
import link.signalapp.error.exception.SignalAppException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecaptchaVerifier {

    private final RecaptchaParams params;

    public void verify(String token) {
        ResponseEntity<Response> responseEntity = new RestTemplate().postForEntity(
                String.format("%s?secret=%s&response=%s", params.getUrl(), params.getSecret(), token),
                null, Response.class);
        if (responseEntity.getStatusCode().value() != 200) {
            throw new RuntimeException("Recaptcha server response error");
        }
        if (!responseEntity.hasBody() || responseEntity.getBody() == null) {
            throw new RuntimeException("Recaptcha server empty response");
        }
        if (!responseEntity.getBody().isSuccess()) {
            throw new SignalAppException(SignalAppErrorCode.RECAPTCHA_TOKEN_NOT_VERIFIED, null);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class Response {
        private boolean success;
        private LocalDateTime challengeTs;
        private String hostname;
        private List<String> errorCodes;
    }

}
