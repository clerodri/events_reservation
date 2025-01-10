package com.clerodri.core.domain.usecase.reservation;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.model.Reservation;
import com.clerodri.core.domain.model.ReservationStatus;
import com.clerodri.core.domain.model.UserModel;
import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.core.domain.repository.ReservationRepository;
import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.core.exception.*;


public class CancelReservationUseCaseImpl  implements CancelReservationUseCase{

   private final ReservationRepository reservationRepository;
   private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public CancelReservationUseCaseImpl(ReservationRepository reservationRepository,
                                        UserRepository userRepository,
                                        EventRepository eventRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public void cancelReservation(Long reservationId, String username) {
        //If the reservation ID is invalid,return 404 Not Found
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(()-> new ReservationNotFoundException("Reservation with ID:"+ reservationId + " not found"));

        //If the reservation does not belong to the user, return 403 Forbidden.
        if(!reservationBelongsToUser(username,reservation.getUserId())){
            throw  new ReservationForbiddenException("The reservation don't belong to the user:"+username);
        }

        //If the reservation its already cancelled, return 409
        if(reservation.getStatus().equals(ReservationStatus.CANCELLED)){
            throw  new ReservationConflictException("The reservation with ID:"+reservationId+" its already CANCELLED");
        }

        Event event = eventRepository.findById(reservation.getEventId())
                .orElseThrow(()-> new EventNotFoundException("Event with ID:"+ reservation.getEventId() +" not found"));
        event.cancelReservation(reservation);

        eventRepository.save(event);
    }

    private boolean reservationBelongsToUser(String username, Long userId){
        UserModel userModel = userRepository.findByUsername(username).orElseThrow();
        return userId.equals(userModel.getId());
    }


}
