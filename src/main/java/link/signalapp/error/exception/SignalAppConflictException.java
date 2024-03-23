package link.signalapp.error.exception;

import link.signalapp.error.code.SignalAppErrorCode;
import lombok.Getter;

@Getter
public class SignalAppConflictException extends SignalAppException {

    public SignalAppConflictException(SignalAppErrorCode errorCode, Object params) {
        super(errorCode, params);
    }

}
