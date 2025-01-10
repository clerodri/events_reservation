package com.clerodri.web.controller;

import com.clerodri.core.domain.model.Event;
import com.clerodri.core.domain.usecase.event.*;
import com.clerodri.web.dto.request.RequestEventDTO;
import com.clerodri.web.dto.response.ResponseAttendeesDTO;
import com.clerodri.web.dto.response.ResponseEventDTO;
import com.clerodri.web.mapper.EventWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event Management", description = "Operations related to events.")
public class EventController {
    private final CreateEventUseCase createEventUseCase;
    private final GetAllEventsUseCase getAllEventsUseCase;
    private final GetEventDetailUseCase getEventDetailUseCase;
    private final UpdateEventUseCase updateEventUseCase;
    private final DeleteEventUseCase deleteEventUseCase;
    private final SearchEventUseCase searchEventUseCase;
    private final AllAttendeesUseCase allAttendeesUseCase;
    private final EventWebMapper eventWebMapper;

    @Operation(
            summary = "Create a new event",
            description = "Creates a new event with the provided details (name, description, date, location, capacity).",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Event created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseEventDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ResponseEventDTO> createEvent(@Valid @RequestBody  RequestEventDTO requestEvent){

        Event event = eventWebMapper.toDomain(requestEvent);
        Event createdEvent = createEventUseCase.create(event);
        ResponseEventDTO response = eventWebMapper.toWeb(createdEvent);
        log.info("WEB - EVENT createEvent {}",response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    @Operation(
            summary = "Retrieve all events",
            description = "Retrieves a list of all events with basic details like name, description, date, location, capacity, and availability.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of events",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseEventDTO.class)
                            )
                    )
            }
    )
    public ResponseEntity<List<ResponseEventDTO>> getAllEvents(){
        List<ResponseEventDTO> response = getAllEventsUseCase.findAll().stream().map(eventWebMapper::toWeb).toList();
        log.info("WEB - EVENT getAllEvents {}",response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Get event details by ID",
            description = "Retrieve the details of a specific event by its ID.",
            parameters = {
                    @Parameter(name = "eventId", description = "The ID of the event to retrieve", required = true, schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK - ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseEventDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "NOT FOUND - ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/{eventId}")
    public ResponseEntity<ResponseEventDTO> getEventById(@PathVariable("eventId") Long eventId)  {
        Event event = getEventDetailUseCase.getEventDetails(eventId);
        ResponseEventDTO response = eventWebMapper.toWeb(event);
        log.info("WEB - EVENT getEventById {}",response);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Update an event by ID",
            description = "Updates an existing event with the provided details (name, description, date, location, capacity).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Event successfully updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseEventDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Event not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PutMapping("/{eventId}")
    public ResponseEntity<ResponseEventDTO> updateEventById(@PathVariable("eventId")Long eventId,
                                                            @Valid @RequestBody RequestEventDTO requestEvent ){
        Event event = eventWebMapper.toDomain(requestEvent);
        Event eventUpdated = updateEventUseCase.updateEvent(eventId, event);
        ResponseEventDTO response = eventWebMapper.toWeb(eventUpdated);

        log.info("WEB - EVENT updateEventById {}",response);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Delete an event by ID",
            description = "Deletes the specified event using the provided event ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "No content",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEventById(@PathVariable("eventId") Long eventId){
        deleteEventUseCase.deleteById(eventId);
        log.info("WEB - EVENT deleteEventById ");
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Search events based on optional filters",
            description = "Search for events with optional filters by name, date, and location.",
            parameters = {
                    @Parameter(name = "name", description = "The name of the event to filter by", required = false, schema = @Schema(type = "string")),
                    @Parameter(name = "date", description = "The date of the event to filter by", required = false, schema = @Schema(type = "string", example = "2024-12-31")),
                    @Parameter(name = "location", description = "The location of the event to filter by", required = false, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK - Successfully retrieved the list of events matching the search criteria",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "Array", implementation = ResponseEventDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/search")
    public ResponseEntity<List<ResponseEventDTO>> searchEvents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String location) {

        List<Event> events = searchEventUseCase.search(name, date, location);

        List<ResponseEventDTO> response = events.stream()
                .map(eventWebMapper::toWeb)
                .toList();

        log.info("WEB - EVENT searchEvents {}",response);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get attendees for a specific event",
            description = "Retrieve the list of attendees for the event specified by the event ID.",
            parameters = {
                    @Parameter(name = "eventId", description = "The ID of the event to retrieve attendees for", required = true, schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "200 OK - ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            type = "array",
                                            implementation = ResponseAttendeesDTO.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "404 NOT FOUND ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "403 FORBIDDEN ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/{eventId}/attendees")
    public ResponseEntity<List<ResponseAttendeesDTO>> attendeesByEvent(@PathVariable("eventId") Long eventId){
        List<ResponseAttendeesDTO> response = allAttendeesUseCase
                .attendeesByEvent(eventId).stream()
                .map(eventWebMapper::toAttendeeWeb).toList();

        log.info("WEB - EVENT attendeesByEvent {}",response);
        return ResponseEntity.ok(response);
    }
}
