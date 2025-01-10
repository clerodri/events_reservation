package com.clerodri.core.domain.usecase.reservation;

import com.clerodri.core.domain.model.Reservation;
import com.clerodri.core.domain.model.UserModel;
import com.clerodri.core.domain.repository.ReservationRepository;
import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.core.exception.UserNotFoundException;

import java.util.List;

public class GetAllUserReservationsUseCaseImpl implements GetAllUserReservationsUseCase {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public GetAllUserReservationsUseCaseImpl(ReservationRepository reservationRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Reservation> reservationsByUser(String userLogged) {
        //1. obtain the user logged from db
        UserModel userModel = userRepository.findByUsername(userLogged)
                .orElseThrow(()-> new UserNotFoundException("User:"+userLogged+" not found in DB"));
        //2. return the list filtered by userID
        return reservationRepository.findAll()
                .stream()
                .filter(r -> r.getUserId().equals(userModel.getId())).toList();
    }
}

