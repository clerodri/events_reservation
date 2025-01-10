package com.clerodri.web.mapper;

import com.clerodri.core.domain.model.Reservation;
import com.clerodri.web.dto.response.ResponseReservationDTO;
import com.clerodri.web.dto.response.ResponseUserReservationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationWebMapper {



    @Mapping(target = "id", source = "reservationId")
    ResponseReservationDTO toWeb(Reservation reservation);


    @Mapping(target = "date", source = "reservationDate")
    @Mapping(target = "id", source = "reservationId")
    ResponseUserReservationDTO toUserWeb(Reservation reservation);
}
