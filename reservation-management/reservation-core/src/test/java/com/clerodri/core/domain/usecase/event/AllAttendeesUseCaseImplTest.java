package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.*;
import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.core.domain.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AllAttendeesUseCaseImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EventRepository eventRepository;

    @InjectMocks
    AllAttendeesUseCaseImpl allAttendeesUseCase;

    private Event event;

    private UserModel user;

    private Reservation reservation;

    @BeforeEach
    void init() {
        event = new Event();
        user = new UserModel();
        user.setId(1L);
        reservation = new Reservation();
        reservation.setEventId(1L);
        reservation.setUserId(1L);
        reservation.setStatus(ReservationStatus.CONFIRMED);
    }

    @Test
    void attendeesByEventShouldReturnAllUsersDetailsSuccessfully() {
        Long eventId = 1L;
        event.setReservations(List.of(reservation));

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findAllById(any())).thenReturn(List.of(user));

        List<AttendeesModel> attendeesModels = allAttendeesUseCase.attendeesByEvent(eventId);


        assertNotNull(attendeesModels.get(0));

    }

    @Test
    void attendeesByEventShouldReturnEventNotFoundException() {
        Long eventId = 1L;


        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EventNotFoundException exception = assertThrows(EventNotFoundException.class,
                ()->{
            allAttendeesUseCase.attendeesByEvent(eventId);
                });

        assertEquals("Event with ID:"+ eventId +" not found", exception.getMessage());

    }

}