package com.clerodri.core.domain.usecase.reservation;

import com.clerodri.core.domain.model.*;
import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.core.domain.repository.ReservationRepository;
import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.core.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CancelReservationUseCaseImplTest {


     @Mock
     ReservationRepository reservationRepository;
     @Mock
     UserRepository userRepository;
     @Mock
     EventRepository eventRepository;

    @InjectMocks
    CancelReservationUseCaseImpl cancelReservationUseCase;



    @Test
    void cancelReservationShouldCancelReservationSuccessfully() {
        Long reservationId=1L;
        String username = "user123";
        Reservation reservation = new Reservation(  reservationId,
                                                    101L,
                                                    21L,
                                                    LocalDateTime.now(),
                                                    ReservationStatus.CONFIRMED);
        Event event = new Event(201L,
                            "Test event",
                            "nada",
                            LocalDateTime.now().plusDays(1),
                            "NYC",
                            100,
                            99,
                            List.of(reservation));


        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByUsername(username)).thenReturn(
                Optional.of(new UserModel(101L, username, "asfd", "asf", RolEnum.USER)));
        when(eventRepository.findById(reservation.getEventId())).thenReturn(Optional.of(event));
        cancelReservationUseCase.cancelReservation(reservationId, username);

        verify(eventRepository).save(any(Event.class));
        assertEquals(ReservationStatus.CANCELLED,event.getReservations().get(0).getStatus());
    }

    @Test
    void cancelReservationShouldThrowExceptionForForbiddenUser() {
        Long reservationId=1L;
        String username = "user123";
        Reservation reservation = new Reservation(  reservationId,
                101L,
                21L,
                LocalDateTime.now(),
                ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByUsername(username)).thenReturn(
                Optional.of(new UserModel(106L, "otherUser", "asfd", "asf", RolEnum.USER)));

        ReservationForbiddenException exception = assertThrows(ReservationForbiddenException.class,()->{
           cancelReservationUseCase.cancelReservation(reservationId, username);
        });

        assertEquals("The reservation don't belong to the user:"+username,exception.getMessage());
    }


    @Test
    void cancelReservationShouldThrowExceptionForReservationConflict() {
        Long reservationId=1L;
        String username = "user123";
        Reservation reservation = new Reservation(  reservationId,
                101L,
                21L,
                LocalDateTime.now(),
                ReservationStatus.CANCELLED);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByUsername(username)).thenReturn(
                Optional.of(new UserModel(101L, username, "asfd", "asf", RolEnum.USER)));

        ReservationConflictException exception = assertThrows(ReservationConflictException.class,()->{
            cancelReservationUseCase.cancelReservation(reservationId, username);
        });

        assertEquals("The reservation with ID:"+reservationId+" its already CANCELLED", exception.getMessage());
    }


    @Test
    void cancelReservationShouldThrowExceptionForReservationNotFound() {
        Long reservationId=1L;
        String username="userTest";

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        ReservationNotFoundException exception = assertThrows(ReservationNotFoundException.class,()->{
            cancelReservationUseCase.cancelReservation(reservationId, username);
        });

        assertEquals("Reservation with ID:"+ reservationId + " not found", exception.getMessage());
    }



}