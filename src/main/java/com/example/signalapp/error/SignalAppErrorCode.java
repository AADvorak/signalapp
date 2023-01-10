package com.example.signalapp.error;

import com.example.signalapp.service.SignalService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SignalAppErrorCode {

    TOO_LONG_FILE("Too long file. Max size is " + 2 * SignalService.MAX_SIGNAL_LENGTH + " bytes."),
    TOO_MANY_SIGNALS_STORED("Too many signals are stored. Max number is "
            + SignalService.MAX_USER_STORED_SIGNALS_NUMBER + " signals.");

    private final String description;

}
