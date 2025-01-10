package com.clerodri.web.config;

import com.clerodri.core.domain.repository.EventRepository;
import com.clerodri.core.domain.repository.ReservationRepository;
import com.clerodri.core.domain.repository.UserRepository;
import com.clerodri.core.domain.service.PasswordService;
import com.clerodri.core.domain.usecase.reservation.*;
import com.clerodri.core.domain.usecase.event.*;
import com.clerodri.core.domain.usecase.user.DeleteUserUseCase;
import com.clerodri.core.domain.usecase.user.DeleteUserUseCaseImpl;
import com.clerodri.core.domain.usecase.user.RegisterUserUseCase;
import com.clerodri.core.domain.usecase.user.RegisterUserUseCaseImpl;
import com.clerodri.web.security.JwtUtils;
import com.clerodri.web.security.UserDetailServiceImpl;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

@OpenAPIDefinition(
        info = @Info(
                title = "Event Reservations REST API",
                description = "The goal of this project is to develop a REST API for managing event reservations.",
                contact = @Contact(
                        name = "Ronaldo Rodriguez",
                        email = "roro@example.com"
                        ),
                version = "v1.0"
        )
)
@Configuration
public class CoreConfig {

    /*  Uses Cases Beans */

    @Bean
    public CreateEventUseCase createEventUseCase(EventRepository eventRepository) {
        return new CreateEventUseCaseImpl(eventRepository);
    }

    @Bean
    public GetAllEventsUseCase getAllEventsUseCase(EventRepository eventRepository){
        return new GetAllEventsUseCaseImpl(eventRepository);
    }

    @Bean
    public GetEventDetailUseCase getEventDetailUseCase(EventRepository eventRepository){
        return new GetEventDetailUseCaseImpl(eventRepository);
    }

    @Bean
    public UpdateEventUseCase updateEventUsesCase(EventRepository eventRepository){
        return new UpdateEventUseCaseImpl(eventRepository);
    }

    @Bean
    public DeleteEventUseCase deleteEventUseCase(EventRepository eventRepository){
        return new DeleteEventUseCaseImpl(eventRepository);
    }

    @Bean
    public SearchEventUseCase searchEventUseCase(EventRepository eventRepository){
        return new SearchEventUseCaseImpl(eventRepository);
    }

    @Bean
    public AllAttendeesUseCase allAttendeesUseCase(UserRepository userRepository,
                                                       EventRepository eventRepository){
        return new AllAttendeesUseCaseImpl(userRepository,eventRepository);
    }

    @Bean
    public CreateReservationUseCase createReservationUseCase(EventRepository eventRepository,
                                                             UserRepository userRepository){
        return new CreateReservationUseCaseImpl(eventRepository, userRepository);
    }

    @Bean
    public CancelReservationUseCase cancelReservationUseCase(ReservationRepository reservationRepository
                                                            ,UserRepository userRepository
                                                            ,EventRepository eventRepository){
        return new CancelReservationUseCaseImpl(reservationRepository,
                                                    userRepository,
                                                    eventRepository);
    }

    @Bean
    public GetAllUserReservationsUseCase userReservationUseCase(ReservationRepository reservationRepository,
                                                                UserRepository userRepository){
        return new GetAllUserReservationsUseCaseImpl(reservationRepository, userRepository);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository,
                                                   PasswordService passwordService){
        return new RegisterUserUseCaseImpl(userRepository, passwordService);
    }

    @Bean
    public DeleteUserUseCase deleteUserUseCase(UserRepository userRepository){
        return new DeleteUserUseCaseImpl(userRepository);
    }

}
