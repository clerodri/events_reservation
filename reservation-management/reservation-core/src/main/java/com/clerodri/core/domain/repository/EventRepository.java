package com.clerodri.core.domain.repository;

import com.clerodri.core.domain.model.Event;


import java.util.List;
import java.util.Optional;

public interface EventRepository {

    Event save(Event event);
    Optional<Event> findById(Long eventId);
    List<Event> findAll();
    void deleteById(Long eventId);
    List<Event> search(String name, String  date, String location);
}
