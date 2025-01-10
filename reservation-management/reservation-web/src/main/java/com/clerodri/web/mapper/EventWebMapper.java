package com.clerodri.web.mapper;


import com.clerodri.core.domain.model.AttendeesModel;
import com.clerodri.core.domain.model.Event;
import com.clerodri.web.dto.request.RequestEventDTO;
import com.clerodri.web.dto.response.ResponseAttendeesDTO;
import com.clerodri.web.dto.response.ResponseEventDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventWebMapper {

    @Mapping(target = "eventName", source = "name")
    @Mapping(target = "eventDateTime", source = "date", dateFormat = "yyyy-MM-dd HH:mm")
    Event toDomain(RequestEventDTO request);


    @Mapping(target = "id", source = "eventId")
    @Mapping(target = "name", source = "eventName")
    @Mapping(target = "date", source = "eventDateTime", dateFormat = "yyyy-MM-dd HH:mm")
    ResponseEventDTO toWeb(Event event);

    ResponseAttendeesDTO toAttendeeWeb(AttendeesModel attendeesModel);

}
