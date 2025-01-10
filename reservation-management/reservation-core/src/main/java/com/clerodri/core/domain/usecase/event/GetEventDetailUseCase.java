package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;

public interface GetEventDetailUseCase {
    Event getEventDetails(Long eventId);
}
