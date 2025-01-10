package com.clerodri.details.repository.datasource;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Profile("mongo")
public class EventMongoDBRepository implements EventRepository {

    private final Map<Long, Event> events = new HashMap<>();

    private Long generateRandomId() {
        return ThreadLocalRandom.current().nextLong(1, 10);
    }

    @Override
    public Event save(Event event) {
        Long eventId = event.getEventId() != null ? event.getEventId() : generateRandomId();
        event.setEventId(eventId);
        events.put(eventId, event);
        log.info("PERSISTENCE MONGO DB - EVENT save: {}",event);
        return event;
    }

    @Override
    public Optional<Event> findById(Long eventId) {
        log.info("PERSISTENCE MONGO DB - EVENT findById: {}", eventId);
        return Optional.ofNullable(events.get(eventId));
    }

    @Override
    public List<Event> findAll() {
        log.info("PERSISTENCE MONGO DB - EVENT findAll: {}",events.values());
        return new ArrayList<>(events.values());
    }

    @Override
    public void deleteById(Long eventId) {
        log.info("PERSISTENCE MONGO DB - EVENT deleteById: {}", eventId);
        events.remove(eventId);
    }

    @Override
    public List<Event> search(String name, String date, String location) {
        log.info("PERSISTENCE MONGO DB - EVENT search: name={}, date={}, location={}", name, date, location);
        return events.values().stream()
                .filter(event ->
                        (name == null || event.getEventName().equalsIgnoreCase(name)) &&
                                (date == null || event.getEventDateTime().equals(date)) &&
                                (location == null || event.getLocation().equalsIgnoreCase(location)))
                .collect(Collectors.toList());
    }
}
