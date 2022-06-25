package com.example.signalapp.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SignalAppDataErrorCode {

    SIGNAL_DOES_NOT_EXIST("Signal does not exist"),
    EMAIL_ALREADY_EXISTS("Email already exists"),
    WRONG_EMAIL_PASSWORD("Wrong email and password pair"),
    USER_TOKEN_NOT_FOUND("User token does not found");

    private final String description;

}
