package com.clerodri.core.domain.usecase.reservation;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.model.Reservation;
import com.clerodri.core.domain.model.ReservationStatus;
import com.clerodri.core.domain.model.UserModel;
import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.core.domain.repository.ReservationRepository;
import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.core.exception.EventConflictException;
import com.clerodri.core.exception.EventNotFoundException;
import com.clerodri.core.exception.ReservationConflictException;
import com.clerodri.core.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

public class CreateReservationUseCaseImpl implements CreateReservationUseCase {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public CreateReservationUseCaseImpl(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Reservation reserve(Long eventId, String userLogged) {

        //If the event ID is invalid, return 404 Not Found
        Event event = eventRepository.findById(eventId)
                .orElseThrow(()-> new EventNotFoundException("Event with ID:"+ eventId +" not found"));

        //If the user has already reserved a spot for the same event, return 409 Conflict.
        UserModel userModel = userRepository.findByUsername(userLogged)
                .orElseThrow(()-> new UserNotFoundException("User  not found in DB"));

        boolean hasSpotReserved = event.getReservations().stream().anyMatch(r->
                ReservationStatus.CONFIRMED.equals(r.getStatus()) && userModel.getId().equals(r.getUserId()));
        if(hasSpotReserved){
            throw new EventConflictException("Cant reserve, Current user has already reserved a SPOT " +
                    "with EVENT ID:"+eventId);
        }

        //Validate si es full(no reservations) return 409 Conflict
        if( event.getAvailability() == 0 ){
            throw new EventConflictException("Cant reserve, The event is FULL");
        }

        Reservation myReservation = new Reservation(null,
                userModel.getId(),
                eventId,
                LocalDateTime.now(),
                ReservationStatus.CONFIRMED
                );

        event.addReservation(myReservation);
        Event eventUpdated = eventRepository.save(event);
        List<Reservation> reservations = eventUpdated.getReservations();
        return reservations.stream().filter(r->
                r.getUserId().equals(userModel.getId()) &&
                r.getEventId().equals(eventId) )
                .findFirst().orElseThrow(()-> new ReservationConflictException("Reservation was not saved correctly"));
    }


}
