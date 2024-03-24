package link.signalapp.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SignalAppErrorCode {

    TOO_LONG_FILE("Too long file"),
    TOO_LONG_SIGNAL("Too long signal"),
    WRONG_VAW_FORMAT("Wrong wav file format"),
    TOO_MANY_SIGNALS_STORED("Too many signals are stored"),
    TOO_MANY_FOLDERS_CREATED("Too many folders are created"),
    RECAPTCHA_TOKEN_NOT_VERIFIED("Recaptcha token verification failed");

    private final String description;

}
