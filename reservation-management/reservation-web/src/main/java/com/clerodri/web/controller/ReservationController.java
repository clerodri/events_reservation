package com.clerodri.web.controller;

import com.clerodri.core.domain.model.Reservation;
import com.clerodri.core.domain.usecase.reservation.CancelReservationUseCase;
import com.clerodri.core.domain.usecase.reservation.CreateReservationUseCase;
import com.clerodri.core.domain.usecase.reservation.GetAllUserReservationsUseCase;
import com.clerodri.web.dto.request.RequestReservationDTO;
import com.clerodri.web.dto.response.ResponseReservationDTO;
import com.clerodri.web.dto.response.ResponseUserReservationDTO;
import com.clerodri.web.mapper.ReservationWebMapper;
import com.clerodri.web.security.UserDetailServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Handles reservations in the system.")
public class ReservationController {

    private final CreateReservationUseCase createReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;
    private final UserDetailServiceImpl userDetailService;
    private final ReservationWebMapper reservationWebMapper;
    private final GetAllUserReservationsUseCase getAllUserReservationsUseCase;


    @Operation(
            summary = "Create a reservation",
            description = "Allows a user to create a reservation for an event.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Reservation successfully created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseReservationDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping
    public ResponseEntity<?> addReservation(@Valid @RequestBody RequestReservationDTO requestReservationDTO){
        String userLogged = userDetailService.getAuthenticatedUsername();
        Reservation reservation = createReservationUseCase.reserve(requestReservationDTO.eventId(),
                                                                    userLogged);
        ResponseReservationDTO response = reservationWebMapper.toWeb(reservation);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



    @Operation(
            summary = "Cancel a reservation",
            description = "Allows a user to cancel their reservation by providing the reservation ID.",
            parameters = @Parameter(name = "reservationId", description = "ID of the reservation to cancel", required = true),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Reservation successfully canceled"),
                    @ApiResponse(responseCode = "404", description = "Reservation not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> cancelReservationById(@PathVariable("reservationId") Long reservationId){
        String userLogged = userDetailService.getAuthenticatedUsername();

        cancelReservationUseCase.cancelReservation(reservationId, userLogged);

        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Get all reservations for the authenticated user",
            description = "Returns all reservations made by the currently authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of reservations for the user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseUserReservationDTO.class))
                    )
            }
    )
    @GetMapping("/user")
    public ResponseEntity<?> userReservations(){
        String userLogged = userDetailService.getAuthenticatedUsername();
        List<Reservation> userReservations = getAllUserReservationsUseCase.reservationsByUser( userLogged );
        List<ResponseUserReservationDTO> response = userReservations.stream()
                .map(reservationWebMapper::toUserWeb).toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
