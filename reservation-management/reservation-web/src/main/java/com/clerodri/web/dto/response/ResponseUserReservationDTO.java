package com.clerodri.web.dto.response;

public record ResponseUserReservationDTO(
        Long id,
        Long eventId,
        Long userId,
        String date,
        String status
) {
}
