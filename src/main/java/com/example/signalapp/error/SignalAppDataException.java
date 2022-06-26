package com.example.signalapp.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class SignalAppDataException extends Exception {

    private final SignalAppDataErrorCode errorCode;

    private final List<String> fields;

}
