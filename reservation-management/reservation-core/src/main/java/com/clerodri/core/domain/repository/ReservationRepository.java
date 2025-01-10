package com.clerodri.core.domain.repository;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Optional<Reservation> findById(Long reservationId);
    List<Reservation> findAll();
}
