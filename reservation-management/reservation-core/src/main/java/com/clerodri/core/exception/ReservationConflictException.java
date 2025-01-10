package com.clerodri.core.exception;

public class ReservationConflictException extends RuntimeException {

    public ReservationConflictException(String message){
        super(message);
    }
}
