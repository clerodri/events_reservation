package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.model.Reservation;
import com.clerodri.core.domain.model.ReservationStatus;
import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.core.domain.repository.ReservationRepository;
import com.clerodri.core.exception.EventConflictException;
import com.clerodri.core.exception.EventNotFoundException;

import java.util.List;


public class DeleteEventUseCaseImpl implements DeleteEventUseCase {



    private final EventRepository eventRepository;

    public DeleteEventUseCaseImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void deleteById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(()->
                        new EventNotFoundException("Event with ID:"+ eventId +" not found"));

        if(event.hasReservationsConfirmed()){
            throw new EventConflictException("Event with ID:"+eventId+" has reservations CONFIRMED");
        }

        eventRepository.deleteById(eventId);
    }


}
