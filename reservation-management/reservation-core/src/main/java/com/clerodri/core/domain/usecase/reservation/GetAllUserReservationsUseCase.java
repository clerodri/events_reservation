package com.clerodri.core.domain.usecase.reservation;

import com.clerodri.core.domain.model.Reservation;

import java.util.List;

public interface GetAllUserReservationsUseCase {
    List<Reservation> reservationsByUser(String userLogged);
}
