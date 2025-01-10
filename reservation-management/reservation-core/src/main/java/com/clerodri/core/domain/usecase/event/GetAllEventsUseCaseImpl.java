package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.repository.EventRepository;

import java.util.List;


public class GetAllEventsUseCaseImpl  implements GetAllEventsUseCase{
    private final EventRepository eventRepository;

    public GetAllEventsUseCaseImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> findAll() {
        List<Event> events = eventRepository.findAll();
        return eventsWithSpots(events);
    }

    private List<Event> eventsWithSpots(List<Event> events){
        return events.stream().filter(event -> event.getAvailability() > 0).toList();
    }
}
