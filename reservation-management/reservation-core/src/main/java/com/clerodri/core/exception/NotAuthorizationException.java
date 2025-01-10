package com.clerodri.core.exception;

public class NotAuthorizationException extends RuntimeException{
    public NotAuthorizationException(String message) {
        super(message);
    }
}
