package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.core.exception.EventNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetEventDetailUseCaseImplTest {

    @Mock
    EventRepository eventRepository;

    @InjectMocks
    GetEventDetailUseCaseImpl getEventDetailUseCase;



    @Test
    public void getEventDetailsShouldReturnEventDetails(){
        Long eventId = 1L;
        Event event = new Event();
        event.setEventId(1L);
        event.setEventName("TEST");
        event.setAvailability(4);
        event.setCapacity(4);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Event eventWithDetails =  getEventDetailUseCase.getEventDetails(eventId);

        assertEquals(eventId,eventWithDetails.getEventId());
        assertEquals(event.getEventName(),eventWithDetails.getEventName());
        assertEquals(event.getCapacity(),eventWithDetails.getCapacity());
        assertEquals(event.getAvailability(),eventWithDetails.getAvailability());

    }

    @Test
    public void getEventDetailsShouldThrowEventNotFoundException(){
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EventNotFoundException exception = assertThrows(EventNotFoundException.class, ()->{
            getEventDetailUseCase.getEventDetails(eventId);
        });

        assertEquals("Event with ID "+ eventId+" not found",exception.getMessage());

    }

}