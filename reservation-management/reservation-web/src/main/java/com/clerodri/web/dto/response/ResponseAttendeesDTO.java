package com.clerodri.web.dto.response;

public record ResponseAttendeesDTO(
        Long userId,
        String username,
        String email,
        String reservationStatus
) {
}
