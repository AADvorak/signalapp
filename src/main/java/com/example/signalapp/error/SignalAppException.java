package com.example.signalapp.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignalAppException extends Exception {

    private final SignalAppErrorCode errorCode;

}
