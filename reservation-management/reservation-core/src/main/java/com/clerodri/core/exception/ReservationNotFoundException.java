package com.clerodri.core.exception;

public class ReservationNotFoundException extends RuntimeException{
    public ReservationNotFoundException(String reservationNotFound) {
        super(reservationNotFound);
    }
}
