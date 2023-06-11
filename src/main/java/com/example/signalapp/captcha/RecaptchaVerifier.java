package com.example.signalapp.captcha;

import com.example.signalapp.error.*;
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

    public void verify(String token) throws Exception {
        ResponseEntity<Response> responseEntity = new RestTemplate().postForEntity(
                String.format("%s?secret=%s&response=%s", params.getUrl(), params.getSecret(), token),
                null, Response.class);
        if (responseEntity.getStatusCodeValue() != 200) {
            throw new Exception("Recaptcha server response error");
        }
        if (!responseEntity.hasBody() || responseEntity.getBody() == null) {
            throw new Exception("Recaptcha server empty response");
        }
        if (!responseEntity.getBody().isSuccess()) {
            throw new SignalAppDataException(SignalAppDataErrorCode.RECAPTCHA_TOKEN_NOT_VERIFIED);
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
