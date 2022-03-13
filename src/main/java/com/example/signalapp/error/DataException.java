package com.example.signalapp.error;

public class DataException extends Exception {

    private final DataErrorCode errorCode;

    public DataException(DataErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public DataErrorCode getErrorCode() {
        return errorCode;
    }
}
