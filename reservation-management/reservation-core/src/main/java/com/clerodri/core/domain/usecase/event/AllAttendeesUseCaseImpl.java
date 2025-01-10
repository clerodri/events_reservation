package com.clerodri.core.domain.usecase.event;

import com.clerodri.core.domain.model.AttendeesModel;
import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.model.Reservation;
import com.clerodri.core.domain.model.UserModel;
import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.core.exception.EventNotFoundException;
import com.clerodri.core.exception.UserNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class AllAttendeesUseCaseImpl implements AllAttendeesUseCase{

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public AllAttendeesUseCaseImpl(UserRepository userRepository,
                                   EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<AttendeesModel> attendeesByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(()->
                        new EventNotFoundException("Event with ID:"+ eventId +" not found"));
        // get all reservations from specific event
        List<Reservation> reservations = event.getReservations();

        // get all usersIds of reservations list
        Set<Long> userIds = reservations.stream()
                            .map(Reservation::getUserId)
                            .collect(Collectors.toSet());

        List<UserModel> users = userRepository.findAllById(userIds);
        Map<Long, UserModel> mapUsers = users.stream()
                .collect(Collectors.toMap(UserModel::getId, user -> user));

        return reservations.stream()
                .map(reservation -> {
                    UserModel user = mapUsers.get(reservation.getUserId());
                    return new AttendeesModel(user, reservation.getStatus().name());
                }).toList();

    }
}

