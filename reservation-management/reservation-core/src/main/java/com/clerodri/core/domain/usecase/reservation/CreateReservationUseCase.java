package com.clerodri.core.domain.usecase.reservation;

import com.clerodri.core.domain.model.Reservation;

public interface CreateReservationUseCase {

    Reservation reserve(Long eventId, String userLogged);
}
