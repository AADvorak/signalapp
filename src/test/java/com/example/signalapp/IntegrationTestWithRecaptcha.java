package com.example.signalapp;

import com.example.signalapp.captcha.RecaptchaVerifier;
import com.example.signalapp.error.SignalAppDataErrorCode;
import com.example.signalapp.error.SignalAppDataException;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

public class IntegrationTestWithRecaptcha extends IntegrationTestBase {

    protected static final String PROPER_TOKEN = "PROPER_TOKEN";
    protected static final String WRONG_TOKEN = "WRONG_TOKEN";

    @MockBean
    protected RecaptchaVerifier recaptchaVerifier;

    protected void prepareRecaptchaVerifier() throws Exception {
        doNothing().when(recaptchaVerifier).verify(PROPER_TOKEN);
        doThrow(new SignalAppDataException(SignalAppDataErrorCode.RECAPTCHA_TOKEN_NOT_VERIFIED))
                .when(recaptchaVerifier).verify(WRONG_TOKEN);
    }

}
