package com.clerodri.core.domain.usecase.reservation;

public interface CancelReservationUseCase {

    void cancelReservation(Long reservationId, String username);
}
