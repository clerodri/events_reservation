package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.model.Reservation;
import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.core.domain.repository.ReservationRepository;
import com.clerodri.core.exception.EventConflictException;
import com.clerodri.core.exception.EventNotFoundException;

import java.util.List;

public class UpdateEventUseCaseImpl implements UpdateEventUseCase{

    private final EventRepository eventRepository;

    public UpdateEventUseCaseImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;

    }

    @Override
    public Event updateEvent(Long eventId, Event changedEvent) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(()-> new EventNotFoundException("Event with ID:"+ eventId+" not found"));
        List<Reservation> reservations = event.getReservations();
        if (changedEvent.getCapacity() < reservations.size()){
            throw  new EventConflictException("The event capacity can't be lower than event's reservation");
        }

        Event eventUpdated = applyChanges(changedEvent, event);
        return eventRepository.save(eventUpdated);
    }

    private Event applyChanges(Event changedEvent, Event event) {
        event.setEventName(changedEvent.getEventName());
        event.setDescription(changedEvent.getDescription());
        event.setEventDateTime(changedEvent.getEventDateTime());
        event.setLocation(changedEvent.getLocation());
        event.setCapacity(changedEvent.getCapacity());
        event.setAvailability(changedEvent.getCapacity() - event.getReservations().size());
        return event;
    }


}
