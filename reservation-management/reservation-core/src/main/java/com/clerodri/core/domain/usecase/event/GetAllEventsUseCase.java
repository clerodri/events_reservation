package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;

import java.util.List;

public interface GetAllEventsUseCase {

    List<Event> findAll();
}
