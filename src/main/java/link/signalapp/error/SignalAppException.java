package link.signalapp.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignalAppException extends Exception {

    private final SignalAppErrorCode errorCode;
    private final Object params;

}
