package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;

public interface CreateEventUseCase {

    Event create(Event event);
}
