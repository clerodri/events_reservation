package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.AttendeesModel;

import java.util.List;

public interface AllAttendeesUseCase {
    List<AttendeesModel> attendeesByEvent(Long eventId);
}
