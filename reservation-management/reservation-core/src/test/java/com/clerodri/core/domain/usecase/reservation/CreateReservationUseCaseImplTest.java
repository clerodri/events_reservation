package com.clerodri.core.domain.usecase.reservation;

import com.clerodri.core.domain.model.*;
import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.core.exception.EventConflictException;
import com.clerodri.core.exception.EventNotFoundException;
import com.clerodri.core.exception.ReservationForbiddenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CreateReservationUseCaseImplTest {

    @Mock
    EventRepository eventRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    CreateReservationUseCaseImpl createReservationUseCase;




    @Test
    public void reserveShouldCreateReservationSuccessfully(){

        Event event = new Event();
        event.setEventId(1L);
        event.setAvailability(10);

        UserModel user = new UserModel();
        user.setId(1L);
        user.setUsername("testUser");

        Reservation reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setUserId(1L);
        reservation.setEventId(1L);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.CONFIRMED);

        Event eventUpdated = new Event(1L,
                "Test event",
                "nada",
                LocalDateTime.now().plusDays(1),
                "NYC",
                10,
                9,
                List.of(reservation));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findByUsername("testUser")).thenReturn(
                Optional.of(user));
        when(eventRepository.save(event)).thenReturn(eventUpdated);
        Reservation newReservation = createReservationUseCase.reserve(1L,"testUser");

        assertNotNull(newReservation);
        assertEquals(reservation.getStatus(),newReservation.getStatus());
        assertEquals(reservation.getEventId(),newReservation.getEventId());
        assertEquals(reservation.getUserId(),user.getId());
        verify(eventRepository).save(event);

    }

    @Test
    public void reserveShouldThrowConflictExceptionForUserReservedSameEvent(){

        Reservation reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setUserId(1L);
        reservation.setEventId(1L);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.CONFIRMED);

        UserModel user = new UserModel();
        user.setId(1L);
        user.setUsername("testUser");

        Event event = new Event();
        event.setEventId(1L);
        event.addReservation(reservation);


        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findByUsername("testUser")).thenReturn(
                Optional.of(user));



        EventConflictException exception = assertThrows(EventConflictException.class,()->{
             createReservationUseCase.reserve(1L,"testUser");
        });

        assertEquals("Cant reserve, Current user has already reserved a SPOT " +
                "with EVENT ID:"+event.getEventId(),exception.getMessage());

    }


    @Test
    public void reserveShouldThrowNotFoundExceptionForInvalidEventId(){

        Event event = new Event();
        event.setEventId(1L);
        event.setAvailability(10);

        when(eventRepository.findById(event.getEventId())).thenReturn(Optional.empty());

        EventNotFoundException exception = assertThrows(EventNotFoundException.class,()->{
            createReservationUseCase.reserve(1L,"testUser");
        });

        assertEquals("Event with ID:"+ event.getEventId() +" not found",exception.getMessage());

    }


    @Test
    public void reserveShouldThrowConflictExceptionForEventIsFull(){

        Event event = new Event();
        event.setEventId(1L);
        event.setAvailability(0);

        UserModel user = new UserModel();
        user.setId(1L);
        user.setUsername("testUser");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findByUsername("testUser")).thenReturn(
                Optional.of(user));

        EventConflictException exception = assertThrows(EventConflictException.class,()->{
            createReservationUseCase.reserve(1L,"testUser");
        });

        assertEquals("Cant reserve, The event is FULL",exception.getMessage());

    }
}