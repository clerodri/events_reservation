package com.clerodri.details.repository.jpa;

import com.clerodri.details.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {

}
