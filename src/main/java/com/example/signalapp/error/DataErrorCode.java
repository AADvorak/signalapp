package com.example.signalapp.error;

public enum DataErrorCode {

    SIGNAL_DOES_NOT_EXIST("Signal does not exist");

    private final String description;

    DataErrorCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
