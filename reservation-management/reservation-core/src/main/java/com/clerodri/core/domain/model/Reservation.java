package com.clerodri.core.domain.model;

import java.time.LocalDateTime;

public class Reservation {

    private Long reservationId;
    private Long userId;
    private Long eventId;
    private LocalDateTime reservationDate;
    private ReservationStatus status;

    public Reservation(){}

    public Reservation(Long reservationId, Long userId, Long eventId, LocalDateTime reservationDate, ReservationStatus status) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.eventId = eventId;
        this.reservationDate = reservationDate;
        this.status = status;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
