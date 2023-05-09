package com.example.signalapp.error;

import lombok.Getter;

@Getter
public class SignalAppException extends Exception {

    private final SignalAppErrorCode errorCode;
    private final Object params;

    public SignalAppException(SignalAppErrorCode errorCode, Object params) {
        this.errorCode = errorCode;
        this.params = params;
    }
}
