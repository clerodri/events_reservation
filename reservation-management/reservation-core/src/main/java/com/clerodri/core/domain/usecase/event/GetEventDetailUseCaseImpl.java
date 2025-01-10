package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.core.exception.EventNotFoundException;

import java.util.Optional;

public class GetEventDetailUseCaseImpl implements GetEventDetailUseCase{
    private final EventRepository eventRepository;

    public GetEventDetailUseCaseImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event getEventDetails(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(()->{
                    System.out.println("\" SERVICES GET DETAILS  Event not found with ID: {}\", eventId ");
                    return new EventNotFoundException("Event with ID "+ eventId+" not found");
                });
    }


}
