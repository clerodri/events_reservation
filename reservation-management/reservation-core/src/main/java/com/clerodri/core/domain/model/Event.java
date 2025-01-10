package com.clerodri.core.domain.model;

import com.clerodri.core.exception.ReservationNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Event {

    private Long eventId;
    private String eventName;
    private String description;
    private LocalDateTime eventDateTime;
    private String location;
    private int capacity;
    private int availability;
    private List<Reservation> reservations;

    public Event(){
        this.reservations = new ArrayList<>();
    }

    public Event(Long eventId, String eventName, String description,
                 LocalDateTime eventDateTime, String location,
                 int capacity, int availability, List<Reservation> reservations
    ) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.description = description;
        this.eventDateTime = eventDateTime;
        this.location = location;
        this.capacity = capacity;
        this.availability = availability;
        this.reservations = reservations !=null ? reservations : new ArrayList<>();
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void addReservation(Reservation reservation){
        this.reservations.add(reservation);
        this.availability--;
    }

    public void cancelReservation(Reservation reservation){
        if( reservation == null ){
            throw new ReservationNotFoundException("Reservation cannot be null");
        }
        this.reservations.stream()
                .filter(r -> r.getReservationId().equals(reservation.getReservationId()))
                .forEach(r -> r.setStatus(ReservationStatus.CANCELLED));
        this.availability++;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public boolean hasReservationsConfirmed(){
        return this.reservations.stream().anyMatch(r->
                ReservationStatus.CONFIRMED.equals(r.getStatus()));
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", description='" + description + '\'' +
                ", eventDateTime=" + eventDateTime +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", availability=" + availability +
                '}';
    }
}
