package com.example.signalapp.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignalAppDataException extends Exception {

    private final SignalAppDataErrorCode errorCode;

}
