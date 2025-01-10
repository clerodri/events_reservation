package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SearchEventUseCaseImplTest {

    @Mock
    EventRepository eventRepository;


    @InjectMocks
    SearchEventUseCaseImpl searchEventUseCase;

    private Event event;

    @BeforeEach
    public void init(){
        event = new Event();

    }

    @Test
    public void searchEventsByNameReturnsSuccessfullyList(){

        String name="TEST";
        event.setEventName("TEST");
        when(eventRepository.search(name,null,null)).thenReturn(List.of(event));

        List<Event> eventsByName = searchEventUseCase.search("TEST",null,null);

        assertNotNull(eventsByName);
        assertEquals(1,eventsByName.size());
        assertEquals(event.getEventName(),eventsByName.get(0).getEventName());

    }

    @Test
    public void searchEventsByNameReturnEmptyList(){

        String name="TEST";
        when(eventRepository.search(name,null,null)).thenReturn(List.of());

        List<Event> eventsByName = searchEventUseCase.search(name,null,null);

        assertNotNull(eventsByName);
        assertEquals(0,eventsByName.size());

    }

    @Test
    public void searchEventsByLocationReturnsSuccessfullyList(){

        String location="GYQ";
        event.setEventName("TEST");
        event.setLocation(location);

        when(eventRepository.search(null,null,location)).thenReturn(List.of(event));

        List<Event> eventsByName = searchEventUseCase.search(null,null,location);

        assertNotNull(eventsByName);
        assertEquals(1,eventsByName.size());
        assertEquals("TEST",eventsByName.get(0).getEventName());

    }

    @Test
    public void searchEventsByDateReturnsSuccessfullyList(){

        String date="2024-12-02T10:00:00";
        event.setEventName("TEST");
        event.setEventDateTime(LocalDateTime.parse(date));

        when(eventRepository.search(null,date,null)).thenReturn(List.of(event));

        List<Event> eventsByName = searchEventUseCase.search(null,date,null);

        assertNotNull(eventsByName);
        assertEquals(1,eventsByName.size());
        assertEquals("TEST",eventsByName.get(0).getEventName());
    }
}