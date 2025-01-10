package com.clerodri.details.mapper;

import com.clerodri.core.domain.model.Event;
import com.clerodri.details.entity.EventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring", uses = {ReservationMapper.class})
public interface EventMapper {


    Event toDomain(EventEntity eventEntity);

    EventEntity toEntity(Event event);
}
