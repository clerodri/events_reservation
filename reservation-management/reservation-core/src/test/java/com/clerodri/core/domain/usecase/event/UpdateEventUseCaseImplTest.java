package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.model.Reservation;
import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.core.exception.EventConflictException;
import com.clerodri.core.exception.EventNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateEventUseCaseImplTest {
    @Mock
    EventRepository eventRepository;

    @InjectMocks
    UpdateEventUseCaseImpl updateEventUseCase;

    private Event event;
    private Event eventChanged;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setEventName("Test Event");
        event.setCapacity(4);
        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();
        event.setReservations(Arrays.asList(reservation1, reservation2));


        eventChanged = new Event();
        eventChanged.setEventName("Updated Event TEST");

    }


    @Test
    public void updateEventShouldChangeEventSuccessfully(){
        Long eventId=1L;
        eventChanged.setCapacity(3);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);


        Event result = updateEventUseCase.updateEvent(eventId, eventChanged);

        assertNotNull(result);
        assertEquals(eventChanged.getCapacity(), result.getCapacity());
        verify(eventRepository, times(1)).save(result);
    }



    @Test
    public void updateEventThrowEventNotFoundException(){
        Long eventId=1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            updateEventUseCase.updateEvent(eventId,new Event());
        });

        assertEquals("Event with ID:"+ eventId+" not found",exception.getMessage());

    }



    @Test
    public void updateEventShouldThrowEventConflictException(){
        Long eventId=1L;
        event.setCapacity(2);
        eventChanged.setCapacity(1);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));


        EventConflictException exception = assertThrows(EventConflictException.class, () -> {
             updateEventUseCase.updateEvent(eventId, eventChanged);
        });

        assertEquals("The event capacity can't be lower than event's reservation",
                exception.getMessage());

    }




}