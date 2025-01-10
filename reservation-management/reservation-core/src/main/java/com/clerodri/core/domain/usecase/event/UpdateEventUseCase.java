package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;

public interface UpdateEventUseCase {

    Event updateEvent(Long eventId,  Event event);
}
