package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.repository.EventRepository;

import java.util.List;

public class SearchEventUseCaseImpl  implements  SearchEventUseCase{


    private final EventRepository eventRepository;

    public SearchEventUseCaseImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }


    @Override
    public List<Event> search(String name, String date, String location) {
        return eventRepository.search(name, date,location);
    }
}
