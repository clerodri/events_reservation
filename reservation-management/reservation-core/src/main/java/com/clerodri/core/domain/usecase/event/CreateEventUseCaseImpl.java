package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.repository.EventRepository;


public class CreateEventUseCaseImpl  implements CreateEventUseCase{
     private final EventRepository eventRepository;

    public CreateEventUseCaseImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event create(Event event) {
        // set capacity equals to availability
        event.setAvailability(event.getCapacity());
        return  eventRepository.save(event);
    }
}
