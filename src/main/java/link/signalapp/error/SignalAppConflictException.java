package link.signalapp.error;

import lombok.Getter;

@Getter
public class SignalAppConflictException extends SignalAppException {

    public SignalAppConflictException(SignalAppErrorCode errorCode) {
        super(errorCode, null);
    }

    public SignalAppConflictException(SignalAppErrorCode errorCode, Object params) {
        super(errorCode, params);
    }

}
