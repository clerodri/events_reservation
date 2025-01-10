package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.model.Reservation;
import com.clerodri.core.domain.model.ReservationStatus;
import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.core.exception.EventConflictException;
import com.clerodri.core.exception.EventNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteEventUseCaseImplTest {

    @Mock
    EventRepository eventRepository;

    @InjectMocks
    DeleteEventUseCaseImpl deleteEventUseCase;

    private Event  event;
   private Reservation reservation;
    @BeforeEach
    public void init(){
        event = new Event();
        event.setEventName("EVENT TEST");

        reservation = new Reservation();
        reservation.setStatus(ReservationStatus.CONFIRMED);

    }

    @Test
    public void deleteByIdShouldDeleteEventSuccessfully(){
        Long eventId=1L;


        when(eventRepository.findById(eventId)).thenReturn(Optional.ofNullable(event));

        doNothing().when(eventRepository).deleteById(eventId);
        deleteEventUseCase.deleteById(eventId);

        verify(eventRepository,times(1)).deleteById(1L);

    }

    @Test
    public void deleteByIdShouldThrowEventNotFoundException(){
        Long eventId=1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());



        EventNotFoundException exception = assertThrows(EventNotFoundException.class, ()
        -> {
            deleteEventUseCase.deleteById(eventId);
        });

        assertEquals("Event with ID:"+ eventId +" not found",exception.getMessage());

    }
    @Test
    public void deleteByIdShouldThrowEventConflictException(){
        Long eventId=1L;
        event.setReservations(List.of(reservation));

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        EventConflictException exception = assertThrows(EventConflictException.class, ()
                -> {
            deleteEventUseCase.deleteById(eventId);
        });

        assertEquals("Event with ID:"+eventId+" has reservations CONFIRMED",exception.getMessage());

    }
}