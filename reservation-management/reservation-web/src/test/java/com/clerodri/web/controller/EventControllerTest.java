package com.clerodri.web.controller;

import com.clerodri.core.domain.model.*;
import com.clerodri.core.domain.usecase.event.*;
import com.clerodri.core.exception.EventConflictException;
import com.clerodri.core.exception.EventForbiddenException;
import com.clerodri.core.exception.EventNotFoundException;
import com.clerodri.web.dto.request.RequestEventDTO;
import com.clerodri.web.dto.response.ResponseAttendeesDTO;
import com.clerodri.web.dto.response.ResponseEventDTO;
import com.clerodri.web.exception.DomainExceptionHandler;
import com.clerodri.web.exception.ErrorResponse;
import com.clerodri.web.exception.ValidationExceptionHandler;
import com.clerodri.web.mapper.EventWebMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    MockMvc mockMvc;
    EventController subject;

    CreateEventUseCase createEventUseCase = mock(CreateEventUseCase.class);
    GetAllEventsUseCase getAllEventsUseCase = mock(GetAllEventsUseCase.class);
    EventWebMapper eventWebMapper = mock(EventWebMapper.class);
    GetEventDetailUseCase getEventDetailUseCase = mock(GetEventDetailUseCase.class);
    UpdateEventUseCase updateEventUseCase = mock(UpdateEventUseCase.class);
    DeleteEventUseCase deleteEventUseCase = mock(DeleteEventUseCase.class);
    SearchEventUseCase searchEventUseCase = mock(SearchEventUseCase.class);
    AllAttendeesUseCase allAttendeesUseCase = mock(AllAttendeesUseCase.class);

    private RequestEventDTO requestEventDTO;
    private Event event;
    private ResponseEventDTO expected;
    private Event eventCreated;
    private List<ResponseEventDTO> responseList;

    private ObjectMapper objectMapper;

    @BeforeEach
    void init(){
        subject = new EventController(
                createEventUseCase,
                getAllEventsUseCase,
                getEventDetailUseCase,
                updateEventUseCase,
                deleteEventUseCase,
                searchEventUseCase,
                allAttendeesUseCase,
                eventWebMapper
                );
        mockMvc = MockMvcBuilders.standaloneSetup(subject)
                .setControllerAdvice(new DomainExceptionHandler(), new ValidationExceptionHandler())
                .build();


        event = new Event();
        event.setEventId(null);
        event.setEventName("TEST");
        event.setEventDateTime(LocalDateTime.parse("2024-12-15T18:00:00"));
        event.setDescription( "TEST");
        event.setLocation( "GYQ");
        event.setCapacity(10);

        eventCreated = new Event();
        eventCreated.setEventId(1L);
        eventCreated.setEventName("TEST");
        eventCreated.setEventDateTime(LocalDateTime.parse("2024-12-15T18:00:00"));
        eventCreated.setDescription( "TEST");
        eventCreated.setLocation( "GYQ");
        eventCreated.setCapacity(10);

        requestEventDTO = new RequestEventDTO(
                "TEST",
                "TESTING CONTROLLER",
                LocalDateTime.parse("2024-12-15T18:00:00"),
                "GUAYAQUIL",
                10
        );
        expected = new ResponseEventDTO(
                1L,
                "TEST",
                "TESTING CONTROLLER",
                "2024-12-15T18:00:00",
                "GUAYAQUIL",
                10,
                10
        );

        objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    /*  TEST CREATE EVENT    */
    @Test
    public void EventController_CreateEvent_WhenSuccess() throws Exception {

          requestEventDTO = new RequestEventDTO(
                "TEST",
                "TESTING CONTROLLER",
                LocalDateTime.parse("2027-12-15T18:00:00"),
                "GUAYAQUIL",
                10
        );


        when(eventWebMapper.toDomain(requestEventDTO)).thenReturn(event);
        when(createEventUseCase.create(event)).thenReturn(eventCreated);
        when(eventWebMapper.toWeb(eventCreated)).thenReturn(expected);

        String requestBody = objectMapper.writeValueAsString(requestEventDTO);


        MvcResult mvcResult = mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO responseEventDTO = objectMapper.readValue(contentAsString,
                ResponseEventDTO.class);

        assertThat(responseEventDTO).isEqualTo(expected);
    }
    @Test
    public void EventController_CreateEvent_WhenInvalidFieldOrMissing() throws Exception {

        RequestEventDTO badRequest = new RequestEventDTO(
                null,
                "",
                LocalDateTime.parse("2024-12-15T18:00:00"),
                "GUAYAQUIL",
                10
        );

        String requestBody = objectMapper.writeValueAsString(badRequest);

        MvcResult mvcResult = mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(contentAsString,
                ErrorResponse.class);

        assertThat(errorResponse.getDetails().get("name"))
                .isEqualTo("Event name is required");

    }


    /*  TEST GET EVENT BY ID    */
    @Test
    public void EventController_GetEventById_WhenSuccess() throws Exception{
        Long eventId = 1L;

        when(getEventDetailUseCase.getEventDetails(eventId)).thenReturn(event);
        when(eventWebMapper.toWeb(event)).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/api/events/{eventId}",eventId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

       String contentAsString = result.getResponse().getContentAsString();
       ResponseEventDTO responseEventDTO = objectMapper.readValue(contentAsString,
                                                                ResponseEventDTO.class);

       assertThat(responseEventDTO).isEqualTo(expected);

    }
    @Test
    public void EventController_GetEventById_WhenEventNotExists() throws Exception{

        Long eventId = 1L;
        ErrorResponse expected = new ErrorResponse(
                null,
                HttpStatus.NOT_FOUND.value(),
                "Error, Event doesn't exist",
                "Event with ID:"+ eventId+" not found",
                null
        );

        when(getEventDetailUseCase.getEventDetails(eventId))
                .thenThrow(new EventNotFoundException("Event with ID:"+ eventId+" not found"));

        MvcResult result = mockMvc.perform(get("/api/events/{eventId}",eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(contentAsString,
                ErrorResponse.class);

        assertThat(errorResponse.getError()).isEqualTo(expected.getError());
        assertThat(errorResponse.getMessage()).isEqualTo(expected.getMessage());


    }
    @Test

    /*  TEST UPDATE EVENT BY ID    */
    public void EventController_UpdateEventById_WhenSuccess() throws Exception {
        Long eventId = 1L;

        RequestEventDTO requestEventDTO = new RequestEventDTO(
          "TEST UPDATED",
          "TEST UPDATED",
          LocalDateTime.parse("2027-12-15T10:00:00"),
          "CUENCA",
          150
        );

        Event eventWithOutId = new Event();
        eventWithOutId.setEventId(null);
        eventWithOutId.setEventName("TEST UPDATED");
        eventWithOutId.setEventDateTime(LocalDateTime.parse("2027-12-15T10:00:00"));
        eventWithOutId.setDescription( "TEST UPDATED");
        eventWithOutId.setLocation( "CUENCA");
        eventWithOutId.setCapacity(150);
        eventWithOutId.setAvailability(150);

        Event eventUpdated = new Event();
        eventUpdated.setEventId(eventId);
        eventUpdated.setEventName("TEST UPDATED");
        eventUpdated.setEventDateTime(LocalDateTime.parse("2027-12-15T10:00:00"));
        eventUpdated.setDescription( "TEST UPDATED");
        eventUpdated.setLocation( "CUENCA");
        eventUpdated.setCapacity(150);
        eventUpdated.setAvailability(150);

        ResponseEventDTO responseEventDTO = new ResponseEventDTO(
                eventId,
                "TEST UPDATED",
                "TEST UPDATED",
                "2027-12-15T10:00:00",
                "CUENCA",
                150,
                150
        );

        when(eventWebMapper.toDomain(requestEventDTO)).thenReturn(eventWithOutId);
        when(updateEventUseCase.updateEvent(eventId,eventWithOutId)).thenReturn(eventUpdated);
        when(eventWebMapper.toWeb(eventUpdated)).thenReturn(responseEventDTO);

        String requestBody = objectMapper.writeValueAsString(requestEventDTO);
        MvcResult mvcResult = mockMvc.perform(put("/api/events/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ResponseEventDTO response = objectMapper.readValue(contentAsString,
                ResponseEventDTO.class);

        assertThat(response).isEqualTo(responseEventDTO);
    }
    @Test
    public void EventController_UpdateEventById_WhenEventIdIsInvalid() throws Exception {
        Long eventId = 10L;
        RequestEventDTO requestEventDTO = new RequestEventDTO(
                "TEST UPDATED",
                "TEST UPDATED",
                LocalDateTime.parse("2027-12-15T10:00:00"),
                "CUENCA",
                150
        );
        Event eventWithOutId = new Event();
        eventWithOutId.setEventId(null);
        eventWithOutId.setEventName("TEST UPDATED");
        eventWithOutId.setEventDateTime(LocalDateTime.parse("2027-12-15T10:00:00"));
        eventWithOutId.setDescription( "TEST UPDATED");
        eventWithOutId.setLocation( "CUENCA");
        eventWithOutId.setCapacity(150);
        eventWithOutId.setAvailability(150);

        ErrorResponse expected = new ErrorResponse(
                null,
                HttpStatus.NOT_FOUND.value(),
                "Error, Event doesn't exist",
                "Event with ID:"+ eventId+" not found",
                null
        );

        when(eventWebMapper.toDomain(requestEventDTO)).thenReturn(eventWithOutId);
        when(updateEventUseCase.updateEvent(eventId,eventWithOutId)).thenThrow(
                new EventNotFoundException("Event with ID:"+ eventId+" not found")
        );


        String requestBody = objectMapper.writeValueAsString(requestEventDTO);
        MvcResult mvcResult = mockMvc.perform(put("/api/events/{eventId}", eventId)
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
    public void EventController_UpdateEventById_WhenCapacityLowerThanReservations() throws Exception {
        Long eventId = 1L;
        RequestEventDTO requestEventDTO = new RequestEventDTO(
                "TEST UPDATED",
                "TEST UPDATED",
                LocalDateTime.parse("2027-12-15T10:00:00"),
                "CUENCA",
                150
        );
        Event eventWithOutId = new Event();
        eventWithOutId.setEventId(null);
        eventWithOutId.setEventName("TEST UPDATED");
        eventWithOutId.setEventDateTime(LocalDateTime.parse("2027-12-15T10:00:00"));
        eventWithOutId.setDescription( "TEST UPDATED");
        eventWithOutId.setLocation( "CUENCA");
        eventWithOutId.setCapacity(150);
        eventWithOutId.setAvailability(150);

        ErrorResponse expected = new ErrorResponse(
                null,
                HttpStatus.CONFLICT.value(),
                "Error, Conflict",
                "The event capacity can't be lower than event's reservation",
                null
        );

        when(eventWebMapper.toDomain(requestEventDTO)).thenReturn(eventWithOutId);
        when(updateEventUseCase.updateEvent(eventId,eventWithOutId)).thenThrow(
         new EventConflictException("The event capacity can't be lower than event's reservation")
        );


        String requestBody = objectMapper.writeValueAsString(requestEventDTO);
        MvcResult mvcResult = mockMvc.perform(put("/api/events/{eventId}", eventId)
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
    public void EventController_UpdateEventById_WhenUserLacksPrivileges() throws Exception {
        Long eventId = 10L;
        RequestEventDTO requestEventDTO = new RequestEventDTO(
                "TEST UPDATED",
                "TEST UPDATED",
                LocalDateTime.parse("2027-12-15T10:00:00"),
                "CUENCA",
                150
        );
        Event eventWithOutId = new Event();
        eventWithOutId.setEventId(null);
        eventWithOutId.setEventName("TEST UPDATED");
        eventWithOutId.setEventDateTime(LocalDateTime.parse("2027-12-15T10:00:00"));
        eventWithOutId.setDescription( "TEST UPDATED");
        eventWithOutId.setLocation( "CUENCA");
        eventWithOutId.setCapacity(150);
        eventWithOutId.setAvailability(150);

        ErrorResponse expected = new ErrorResponse(
                null,
                HttpStatus.FORBIDDEN.value(),
                "Error, Forbidden",
                "user lacks admin privileges",
                null
        );

        when(eventWebMapper.toDomain(requestEventDTO)).thenReturn(eventWithOutId);
        when(updateEventUseCase.updateEvent(eq(eventId),any(Event.class))).thenThrow(
                new EventForbiddenException("user lacks admin privileges")
        );


        String requestBody = objectMapper.writeValueAsString(requestEventDTO);
        MvcResult mvcResult = mockMvc.perform(put("/api/events/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isForbidden()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(contentAsString,
                ErrorResponse.class);

        assertThat(errorResponse.getError()).isEqualTo(expected.getError());
        assertThat(errorResponse.getMessage()).isEqualTo(expected.getMessage());

    }


    /*  TEST GET ALL EVENT    */
    @Test
    public void EventController_GetAllEvents_WhenSuccess() throws Exception {
        event.setEventId(1L);
        List<Event> events = List.of(event);

        ResponseEventDTO expectedDTO = new ResponseEventDTO(1L,
                "TEST",
                "TEST",
                "2024-12-15T18:00:00",
                "GYQ",
                10,
                10);
        List<ResponseEventDTO> expectedResponse = List.of(expectedDTO);


        when(getAllEventsUseCase.findAll()).thenReturn(events);
        when(eventWebMapper.toWeb(event)).thenReturn(expectedDTO);



        MvcResult mvcResult = mockMvc.perform(get("/api/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<ResponseEventDTO> responseEventDTO = objectMapper.readValue(contentAsString,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ResponseEventDTO.class));


        assertThat(responseEventDTO).isEqualTo(expectedResponse);
        verify(eventWebMapper,times(1)).toWeb(event);
    }
    @Test
    public void EventController_GetAllEvents_ReturnOnlySortedElementsByDate() throws Exception {
        event.setEventId(1L);
        List<Event> events = List.of(event);

        ResponseEventDTO expectedDTO = new ResponseEventDTO(1L,
                "TEST",
                "TEST",
                "2024-12-15T18:00:00",
                "GYQ",
                10,
                10);
        List<ResponseEventDTO> expectedResponse = List.of(expectedDTO);


        when(getAllEventsUseCase.findAll()).thenReturn(events);
        when(eventWebMapper.toWeb(event)).thenReturn(expectedDTO);



        MvcResult mvcResult = mockMvc.perform(get("/api/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<ResponseEventDTO> responseEventDTO = objectMapper.readValue(contentAsString,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ResponseEventDTO.class));


        assertThat(responseEventDTO).isEqualTo(expectedResponse);
        verify(eventWebMapper,times(1)).toWeb(event);
    }
    @Test
    public void EventController_GetAllEvents_ReturnAllEventsWithAvailabilityGreaterThanZero() throws Exception {
        event.setEventId(1L);
        List<Event> events = List.of(event);

        ResponseEventDTO expectedDTO = new ResponseEventDTO(1L,
                "TEST",
                "TEST",
                "2024-12-15T18:00:00",
                "GYQ",
                10,
                10);
        List<ResponseEventDTO> expectedResponse = List.of(expectedDTO);


        when(getAllEventsUseCase.findAll()).thenReturn(events);
        when(eventWebMapper.toWeb(event)).thenReturn(expectedDTO);



        MvcResult mvcResult = mockMvc.perform(get("/api/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<ResponseEventDTO> responseEventDTO = objectMapper.readValue(contentAsString,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ResponseEventDTO.class));


        assertThat(responseEventDTO).isEqualTo(expectedResponse);
        verify(eventWebMapper,times(1)).toWeb(event);
    }


    /*  TEST DELETE EVENT  BY ID  */
    @Test
    public void EventController_DeleteEventById_WhenSuccess() throws Exception{
        Long eventId = 1L;
        doNothing().when(deleteEventUseCase).deleteById(eventId);

        mockMvc.perform(delete("/api/events/{eventId}",eventId))
                .andExpect(status().isNoContent());

        verify(deleteEventUseCase).deleteById(eventId);
    }
    @Test
    public void EventController_DeleteEventById_WhenEventIdIsInvalid() throws Exception{
        Long eventId = 1L;

        doThrow(new EventNotFoundException("Event with ID:"+ eventId+" not found"))
                .when(deleteEventUseCase).deleteById(eventId);

        mockMvc.perform(delete("/api/events/{eventId}",eventId))
                .andExpect(status().isNotFound());

    }
    @Test
    public void EventController_DeleteEventById_WhenEventHasReservationsConfirmed() throws Exception{
        Long eventId = 1L;
        ErrorResponse expected = new ErrorResponse(
                null,
                HttpStatus.CONFLICT.value(),
                "Error, Conflict",
                "Event with ID:"+eventId+" has reservations CONFIRMED",
                null
        );
        doThrow(new EventConflictException("Event with ID:"+eventId+" has reservations CONFIRMED"))
                .when(deleteEventUseCase).deleteById(eventId);

        MvcResult mvcResult = mockMvc.perform(delete("/api/events/{eventId}", eventId))
                .andExpect(status().isConflict()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(contentAsString,
                ErrorResponse.class);

        assertThat(errorResponse.getError()).isEqualTo(expected.getError());
        assertThat(errorResponse.getMessage()).isEqualTo(expected.getMessage());
    }


    /*  TEST SEARCH  EVENT    */
    @Test
    public  void EventController_SearchEvents_ReturnEventsByName() throws  Exception{
        String name = "MISA";
        Event event = new Event(
                1L,
                name,
                "REUNION CON JESUS",
                LocalDateTime.parse("2025-12-10T10:00"),
                "LA PRADERA",
                30,
                30,null
        );

        ResponseEventDTO responseDto = new ResponseEventDTO(
                1L,
                name,
                "REUNION CON JESUS",
                "2025-12-10T10:00",
                "LA PRADERA",
                30,
                30
        );
        List<ResponseEventDTO> expected = List.of(responseDto);

        when(eventWebMapper.toWeb(event)).thenReturn(responseDto);
        when(searchEventUseCase.search(name,null,null)).thenReturn(List.of(event));

        MvcResult mvcResult = mockMvc.perform(get("/api/events/search")
                        .param("name", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<ResponseEventDTO> responseEventDTO = objectMapper.readValue(contentAsString,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ResponseEventDTO.class));

        assertThat(responseEventDTO).isEqualTo(expected);
    }
    @Test
    public  void EventController_SearchEvents_ReturnEventsByLocation() throws  Exception{
        String location= "LA PRADERA";
        Event event = new Event(
                1L,
                "MISA",
                "REUNION CON JESUS",
                LocalDateTime.parse("2025-12-10T10:00"),
                location,
                30,
                30,null
        );

        ResponseEventDTO responseDto = new ResponseEventDTO(
                1L,
                "MISA",
                "REUNION CON JESUS",
                "2025-12-10T10:00",
                location,
                30,
                30
        );

        when(eventWebMapper.toWeb(event)).thenReturn(responseDto);

        when(searchEventUseCase.search(null,null,location)).thenReturn(List.of(event));

        mockMvc.perform(get("/api/events/search")
                        .param("location", location)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].location").value("LA PRADERA"));
    }
    @Test
    public  void EventController_SearchEvents_ReturnEventsByDate() throws  Exception{
        String date = "2025-12-10T10:00";
        Event event = new Event(
                1L,
                "MISA",
                "REUNION CON JESUS",
                LocalDateTime.parse(date),
                "LA PRADERA",
                30,
                30,null
        );

        ResponseEventDTO responseDto = new ResponseEventDTO(
                1L,
                "MISA",
                "REUNION CON JESUS",
                date,
                "LA PRADERA",
                30,
                30
        );

        when(eventWebMapper.toWeb(event)).thenReturn(responseDto);

        when(searchEventUseCase.search(null,date,null)).thenReturn(List.of(event));

        mockMvc.perform(get("/api/events/search")
                        .param("date", date)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].date").value("2025-12-10T10:00"));
    }
    @Test
    public  void EventController_SearchEvents_ReturnEventsByDateByNameByLocation() throws  Exception{
        String name = "MISA";
        String date = "2025-12-10T10:00";
        String location = "LA PRADERA";
        Event event = new Event(
                1L,
                "MISA",
                "REUNION CON JESUS",
                LocalDateTime.parse("2025-12-10T10:00"),
                "LA PRADERA",
                30,
                30,null
        );

        ResponseEventDTO responseDto = new ResponseEventDTO(
                1L,
                "MISA",
                "REUNION CON JESUS",
                "2025-12-10T10:00",
                "LA PRADERA",
                30,
                30
        );

        when(eventWebMapper.toWeb(event)).thenReturn(responseDto);

        when(searchEventUseCase.search(name,date,location)).thenReturn(List.of(event));

        mockMvc.perform(get("/api/events/search")
                        .param("name", "MISA")
                        .param("date", "2025-12-10T10:00")
                        .param("location", "LA PRADERA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("MISA"))
                .andExpect(jsonPath("$[0].date").value("2025-12-10T10:00"))
                .andExpect(jsonPath("$[0].location").value("LA PRADERA"));
    }
    @Test
    public  void EventController_SearchEvents_WhenNotResultsFound() throws  Exception{
        String name = "MISA";
        String date = "2025-12-10T10:00";
        String location = "LA PRADERA";
        Event event = new Event(
                1L,
                "MISA",
                "REUNION CON JESUS",
                LocalDateTime.parse("2025-12-10T10:00"),
                "LA PRADERA",
                30,
                30,null
        );
        ResponseEventDTO responseDto = new ResponseEventDTO(
                1L,
                "MISA",
                "REUNION CON JESUS",
                "2025-12-10T10:00",
                "LA PRADERA",
                30,
                30
        );
        when(eventWebMapper.toWeb(event)).thenReturn(responseDto);
        when(searchEventUseCase.search(name,date,location)).thenReturn(List.of());

        MvcResult mvcResult = mockMvc.perform(get("/api/events/search")
                        .param("name", "MISA")
                        .param("date", "2025-12-10T10:00")
                        .param("location", "LA PRADERA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        String contentString = mvcResult.getResponse().getContentAsString();
        List<ResponseEventDTO> responseEventDTO = objectMapper.readValue(contentString,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ResponseEventDTO.class));

        assertThat(responseEventDTO).isEqualTo(List.of());

    }


    /*  TEST ATTENDEES BY EVENT    */
    @Test
    public void EventController_AttendeesByEvent_ReturnAllAttendeesByEvent() throws Exception{
        Long eventId = 1L;

        AttendeesModel attendeesModel = new AttendeesModel(
                new UserModel(1L,"RORO","1234","roro@gmail.com", RolEnum.USER),
                ReservationStatus.CONFIRMED.name()
        );

        ResponseAttendeesDTO responseAttendeesDTO = new ResponseAttendeesDTO(
          1L,
          "RORO",
          "roro@gmail.com",
          "CONFIRMED"
        );
        List<AttendeesModel> response = List.of(attendeesModel);


        when(eventWebMapper.toAttendeeWeb(attendeesModel)).thenReturn(responseAttendeesDTO);
        when(allAttendeesUseCase.attendeesByEvent(eventId)).thenReturn(response);

        mockMvc.perform(get("/api/events/{eventId}/attendees",eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
    @Test
    public void EventController_AttendeesByEvent_WhenInsufficientPermissions() throws Exception{
        Long eventId = 1L;

        ErrorResponse expected = new ErrorResponse(
                null,
                HttpStatus.FORBIDDEN.value(),
                "Error, Forbidden",
                null,
                null
        );

        when(allAttendeesUseCase.attendeesByEvent(eventId)).thenThrow(
                new EventForbiddenException(null)
        );

        MvcResult mvcResult = mockMvc.perform(get("/api/events/{eventId}/attendees", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isForbidden()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(contentAsString,
                                                            ErrorResponse.class);

        assertThat(errorResponse.getError()).isEqualTo(expected.getError());
        assertThat(errorResponse.getMessage()).isEqualTo(expected.getMessage());
        assertThat(errorResponse.getStatus()).isEqualTo(expected.getStatus());
    }
    @Test
    public void EventController_AttendeesByEvent_WhenEventIdInvalidOrMissing() throws Exception{
        Long eventId = 1L;

        ErrorResponse expected = new ErrorResponse(
                null,
                HttpStatus.NOT_FOUND.value(),
                "Error, Event doesn't exist",
                "Event with ID:"+ eventId +" not found",
                null
        );

        when(allAttendeesUseCase.attendeesByEvent(eventId)).thenThrow(
                new EventNotFoundException("Event with ID:"+ eventId +" not found")
        );

        MvcResult mvcResult = mockMvc.perform(get("/api/events/{eventId}/attendees", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(contentAsString, ErrorResponse.class);

        assertThat(errorResponse.getError()).isEqualTo(expected.getError());
        assertThat(errorResponse.getMessage()).isEqualTo(expected.getMessage());
        assertThat(errorResponse.getStatus()).isEqualTo(expected.getStatus());

    }



}