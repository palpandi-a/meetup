package com.exception;

public class InitializationException extends RuntimeException {

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(Throwable ex) {
        super(ex);
    }

}
