package com.clerodri.details.repository.jpa;

import com.clerodri.core.domain.model.Reservation;
import com.clerodri.core.domain.repository.ReservationRepository;
import com.clerodri.details.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@Profile({"postgresql","in-memory"})
public class ReservationPersistenceRepository  implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationMapper reservationMapper;


    @Override
    public Optional<Reservation> findById(Long reservationId) {
        log.info("PERSISTENCE - RESERVATION findById:{}",reservationId);

        return reservationJpaRepository.findById(reservationId).map(reservationMapper::toDomain) ;
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> reservations = reservationJpaRepository.findAll()
                .stream().map(reservationMapper::toDomain).toList();
        log.info("PERSISTENCE - RESERVATION findAll:{}",reservations);
        return reservations;
    }

}
