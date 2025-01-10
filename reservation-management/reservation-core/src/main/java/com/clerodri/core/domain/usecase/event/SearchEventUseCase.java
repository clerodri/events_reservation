package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchEventUseCase {
    List<Event> search(String name, String date, String location);
}
