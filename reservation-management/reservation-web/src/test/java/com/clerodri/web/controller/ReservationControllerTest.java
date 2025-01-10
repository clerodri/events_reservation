package com.clerodri.web.controller;

import com.clerodri.core.domain.model.Reservation;
import com.clerodri.core.domain.model.ReservationStatus;
import com.clerodri.core.domain.usecase.reservation.CancelReservationUseCase;
import com.clerodri.core.domain.usecase.reservation.CreateReservationUseCase;
import com.clerodri.core.domain.usecase.reservation.GetAllUserReservationsUseCase;
import com.clerodri.core.exception.EventConflictException;
import com.clerodri.core.exception.EventNotFoundException;
import com.clerodri.core.exception.ReservationForbiddenException;
import com.clerodri.core.exception.ReservationNotFoundException;
import com.clerodri.web.dto.request.RequestReservationDTO;
import com.clerodri.web.dto.response.ResponseReservationDTO;
import com.clerodri.web.exception.DomainExceptionHandler;
import com.clerodri.web.exception.ErrorResponse;
import com.clerodri.web.mapper.ReservationWebMapper;
import com.clerodri.web.security.UserDetailServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    MockMvc mockMvc;

    @Mock
    CreateReservationUseCase createReservationUseCase;
    @Mock
    CancelReservationUseCase cancelReservationUseCase;
    @Mock
    UserDetailServiceImpl userDetailService;
    @Mock
    ReservationWebMapper reservationWebMapper;
    @Mock
    GetAllUserReservationsUseCase getAllUserReservationsUseCase;

    @InjectMocks
    ReservationController subject;
    ObjectMapper objectMapper;

    RequestReservationDTO requestReservationDTO;

    ResponseReservationDTO responseReservationDTO;
    Reservation reservation;
    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(subject)
                .setControllerAdvice(new DomainExceptionHandler())
                .build();

        objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        responseReservationDTO = new ResponseReservationDTO(1L,1L,2L,"CONFIRMED");
        reservation = new Reservation(
                1L,
                2L,
                1L,
                LocalDateTime.parse("2025-12-11T12:22:22"),
                ReservationStatus.CONFIRMED
        );
    }


    /*  TEST ADD RESERVATION   */
    @Test
    void ReservationController_AddReservation_ReturnSuccessfully() throws Exception {
        Long eventId=1L;
        String userLogged ="roro";

        requestReservationDTO = new RequestReservationDTO(eventId);

        when(userDetailService.getAuthenticatedUsername()).thenReturn(userLogged);
        when(createReservationUseCase.reserve(1L,userLogged)).thenReturn(reservation);
        when(reservationWebMapper.toWeb(reservation)).thenReturn(responseReservationDTO);

        String requestBody = objectMapper.writeValueAsString(requestReservationDTO);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void ReservationController_AddReservation_WhenEventIdIsNull() throws Exception {

        requestReservationDTO = new RequestReservationDTO(null);

        String requestBody = objectMapper.writeValueAsString(requestReservationDTO);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
    @Test
    void ReservationController_AddReservation_WhenEventIdInvalid() throws Exception {
        String usserLogger = "roro";
        Long eventId = 11L;
        requestReservationDTO = new RequestReservationDTO(eventId);

        when(userDetailService.getAuthenticatedUsername()).thenReturn(usserLogger);
        when(createReservationUseCase.reserve(eventId,usserLogger )).thenThrow(
                new EventNotFoundException("Event with ID:"+ eventId +" not found")
        );
        String requestBody = objectMapper.writeValueAsString(requestReservationDTO);
        ErrorResponse expected = new ErrorResponse(
               null,
                HttpStatus.NOT_FOUND.value(),
                "Error, Event doesn't exist",
                "Event with ID:"+ eventId +" not found",
                null
        );

        MvcResult mvcResult = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        ErrorResponse errorResponse = objectMapper.readValue(contentAsString,
                ErrorResponse.class);
        assertThat(errorResponse.getError()).isEqualTo(expected.getError());
        assertThat(errorResponse.getMessage()).isEqualTo(expected.getMessage());

    }
    @Test
    void ReservationController_AddReservation_WhenEventIsFull() throws Exception {
        String usserLogger = "roro";
        Long eventId = 11L;
        requestReservationDTO = new RequestReservationDTO(eventId);

        when(userDetailService.getAuthenticatedUsername()).thenReturn(usserLogger);
        when(createReservationUseCase.reserve(eventId,usserLogger )).thenThrow(
                new EventConflictException("Cant reserve, The event is FULL")
        );
        String requestBody = objectMapper.writeValueAsString(requestReservationDTO);
        ErrorResponse expected = new ErrorResponse(
                null,
                HttpStatus.CONFLICT.value(),
                "Error, Conflict",
                "Cant reserve, The event is FULL",
                null
        );

        MvcResult mvcResult = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isConflict()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        ErrorResponse errorResponse = objectMapper.readValue(contentAsString,
                ErrorResponse.class);
        assertThat(errorResponse.getError()).isEqualTo(expected.getError());
        assertThat(errorResponse.getMessage()).isEqualTo(expected.getMessage());

    }
    @Test
    void ReservationController_AddReservation_WhenUserHasAlreadySpotSameEvent() throws Exception {
        String usserLogger = "roro";
        Long eventId = 1L;
        requestReservationDTO = new RequestReservationDTO(eventId);

        when(userDetailService.getAuthenticatedUsername()).thenReturn(usserLogger);
        when(createReservationUseCase.reserve(eventId,usserLogger )).thenThrow(
                new EventConflictException("Cant reserve, Current user has already reserved a SPOT " +
                        "with EVENT ID:"+eventId)
        );
        String requestBody = objectMapper.writeValueAsString(requestReservationDTO);
        ErrorResponse expected = new ErrorResponse(
                null,
                HttpStatus.CONFLICT.value(),
                "Error, Conflict",
                "Cant reserve, Current user has already reserved a SPOT " +
                        "with EVENT ID:"+eventId,
                null
        );

        MvcResult mvcResult = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isConflict()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        ErrorResponse errorResponse = objectMapper.readValue(contentAsString,
                ErrorResponse.class);
        assertThat(errorResponse.getError()).isEqualTo(expected.getError());
        assertThat(errorResponse.getMessage()).isEqualTo(expected.getMessage());

    }




    /*  TEST CANCEL RESERVATION   */
    @Test
    void ReservationController_CancelReservationById_WhenSuccess() throws Exception {
        Long reservationId=1L;
        String userLogged ="roro";

        when(userDetailService.getAuthenticatedUsername()).thenReturn(userLogged);
        doNothing().when(cancelReservationUseCase).cancelReservation(reservationId,userLogged);

        mockMvc.perform(delete("/api/reservations/{reservationId}",reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isNoContent());
    }
    @Test
    void ReservationController_CancelReservationById_WhenReservationIdIsInvalid() throws Exception {
        Long reservationId=1L;
        String userLogged ="roro";
        ErrorResponse expected = new ErrorResponse(
                null,
                HttpStatus.NOT_FOUND.value(),
                "Error, Reservation doesn't exist",
                "Reservation with ID:"+ reservationId + " not found",
                null
        );


        when(userDetailService.getAuthenticatedUsername()).thenReturn(userLogged);
        doThrow(new ReservationNotFoundException("Reservation with ID:"+ reservationId + " not found"))
                .when(cancelReservationUseCase).cancelReservation(reservationId,userLogged);


        MvcResult mvcResult = mockMvc.perform(delete("/api/reservations/{reservationId}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        ErrorResponse errorResponse = objectMapper.readValue(contentAsString,
                ErrorResponse.class);
        assertThat(errorResponse.getError()).isEqualTo(expected.getError());
        assertThat(errorResponse.getMessage()).isEqualTo(expected.getMessage());
    }
    @Test
    void ReservationController_CancelReservationById_WhenReservationNotBelongToUser() throws Exception {
        Long reservationId=1L;
        String userLogged ="roro";
        ErrorResponse expected = new ErrorResponse(
                null,
                HttpStatus.FORBIDDEN.value(),
                "Error, Forbidden",
                "The reservation don't belong to the user:"+userLogged,
                null
        );

        when(userDetailService.getAuthenticatedUsername()).thenReturn(userLogged);
        doThrow(new ReservationForbiddenException("The reservation don't belong to the user:"+userLogged))
                .when(cancelReservationUseCase).cancelReservation(reservationId,userLogged);


        MvcResult mvcResult = mockMvc.perform(delete("/api/reservations/{reservationId}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();

        ErrorResponse errorResponse = objectMapper.readValue(contentAsString,
                ErrorResponse.class);
        assertThat(errorResponse.getError()).isEqualTo(expected.getError());
        assertThat(errorResponse.getMessage()).isEqualTo(expected.getMessage());
    }


}