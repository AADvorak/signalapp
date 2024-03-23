package link.signalapp.error.exception;

import link.signalapp.error.code.SignalAppErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignalAppException extends SignalAppExceptionBase {

    private final SignalAppErrorCode errorCode;
    private final Object params;

}
