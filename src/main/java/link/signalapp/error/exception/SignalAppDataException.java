package link.signalapp.error.exception;

import link.signalapp.error.code.SignalAppDataErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignalAppDataException extends SignalAppExceptionBase {

    private final SignalAppDataErrorCode errorCode;

}
