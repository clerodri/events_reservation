package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAllEventsUseCaseImplTest {

    @Mock
    EventRepository eventRepository;

    @InjectMocks
    GetAllEventsUseCaseImpl getAllEventsUseCase;

    @Test
    public void findAllShouldReturnAllEventsWithSpotsAvailability(){
        Event event1 = new Event();
        event1.setAvailability(5);
        Event event2 = new Event();
        event2.setAvailability(0);

        List<Event> events = List.of(event1,event2);

        when(eventRepository.findAll()).thenReturn(events);

        List<Event> newEventList = getAllEventsUseCase.findAll();

        assertEquals(2, events.size());
        assertEquals(1, newEventList.size());
        assertEquals(event1,newEventList.get(0));
    }

}