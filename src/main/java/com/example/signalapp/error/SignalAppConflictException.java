package com.example.signalapp.error;

import lombok.Getter;

@Getter
public class SignalAppConflictException extends SignalAppException {

    public SignalAppConflictException(SignalAppErrorCode errorCode) {
        super(errorCode);
    }

}
