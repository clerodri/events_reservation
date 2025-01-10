package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;

public interface DeleteEventUseCase {

    void deleteById(Long eventId);
}
