package com.clerodri.details.repository.jpa;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.details.entity.EventEntity;
import com.clerodri.details.filter.EventSpecifications;
import com.clerodri.details.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Repository
@RequiredArgsConstructor
@Profile({"postgresql","in-memory"})
public class EventPersistenceRepository implements EventRepository {

    private final EventJpaRepository eventJpaRepository;
    private final EventMapper eventMapper;


    @Override
    public Event save(Event event) {
        EventEntity myEventEntity = eventMapper.toEntity(event);
        EventEntity saved = eventJpaRepository.save(myEventEntity);
        log.info("PERSISTENCE - EVENT save: {}",saved);
        return eventMapper.toDomain(saved);
    }

    @Override
    public Optional<Event> findById(Long eventId) {
        log.info("PERSISTENCE - EVENT findById: {}",eventId);
        return eventJpaRepository.findById(eventId).map(eventMapper::toDomain);
    }


    @Override
    public List<Event> findAll() {

        List<Event> findAll = eventJpaRepository.findAll(Sort.by(Sort.Order.asc("eventDateTime")))
                .stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());

        log.info("PERSISTENCE - EVENT findAll: {}",findAll);
        return findAll;
    }

    @Override
    public void deleteById(Long eventId) {
        log.info("PERSISTENCE - EVENT deleteById: {}",eventId);
        eventJpaRepository.deleteById(eventId);
    }

    @Override
    public List<Event> search(String name, String date, String location) {
        Specification<EventEntity> filters = Specification
                .where(EventSpecifications.hasName(name))
                    .and(EventSpecifications.hasDate(date))
                    .and(EventSpecifications.hasLocation(location));
        List<EventEntity> entities = eventJpaRepository.findAll(filters);
        log.info("PERSISTENCE - EVENT search: {}",entities);
        return entities.stream()
                .map(eventMapper::toDomain)
                .collect(Collectors.toList());
    }


}
