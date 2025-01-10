package com.clerodri.core.domain.usecase.reservation;

import com.clerodri.core.domain.model.Reservation;
import com.clerodri.core.domain.model.UserModel;
import com.clerodri.core.domain.repository.ReservationRepository;
import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.core.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllUserReservationsUseCaseImplTest {

    @Mock
    ReservationRepository reservationRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    GetAllUserReservationsUseCaseImpl getAllUserReservationsUseCase;


    @Test
    public void reservationsByUserShouldGetAllReservationsOfCurrentUser(){

        UserModel user = new UserModel();
        user.setId(1L);
        user.setUsername("testUser");

        Reservation reservation = new Reservation();
        reservation.setUserId(1L);
        List<Reservation> reservations = List.of(reservation);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<Reservation> newReservationsList = getAllUserReservationsUseCase.reservationsByUser("testUser");

        assertEquals(reservations.size(),newReservationsList.size());
    }

    @Test
    public void reservationsByUserShouldThrowUserNotFoundException(){

        UserModel user = new UserModel();
        user.setId(1L);
        user.setUsername("testUser");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,() ->{
            getAllUserReservationsUseCase.reservationsByUser(user.getUsername());
        });

        assertEquals("User:"+user.getUsername()+" not found in DB",exception.getMessage());
    }
}