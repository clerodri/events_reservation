package com.clerodri.web.dto.response;

public record ResponseReservationDTO(
        Long id,
        Long eventId,
        Long userId,
        String status
) {
}
