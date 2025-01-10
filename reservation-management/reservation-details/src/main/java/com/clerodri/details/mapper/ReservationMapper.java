package com.clerodri.details.mapper;


import com.clerodri.core.domain.model.Reservation;

import com.clerodri.details.entity.EventEntity;
import com.clerodri.details.entity.ReservationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface ReservationMapper {


    @Mapping(target = "eventId", source = "event.eventId")
    Reservation toDomain(ReservationEntity reservationEntity);

    @Mapping(target = "event", source = "eventId", qualifiedByName = "setEventIdToEventEntity")
    ReservationEntity toEntity(Reservation reservation);

    @Named("setEventIdToEventEntity")
    default EventEntity setEventIdToEventEntity(Long eventId){
        if (eventId == null)
        {
            return null;
        }
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventId(eventId);
        return eventEntity;
    }

}
