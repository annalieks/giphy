package com.bsa.bsa_giphy.exception;

public class DataNotFoundException extends RuntimeException {
    private static final String DEFAULT_MSG = "Resource not found";

    public DataNotFoundException() {
        super(DEFAULT_MSG);
    }
}
