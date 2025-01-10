package com.clerodri.core.exception;

public class UserDuplicatedException  extends  RuntimeException{
    public UserDuplicatedException(String message) {
        super(message);
    }
}
