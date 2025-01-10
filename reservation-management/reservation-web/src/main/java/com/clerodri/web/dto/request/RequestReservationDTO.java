package com.clerodri.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RequestReservationDTO(

        @NotNull(message = "Event Id es required")
        @Positive
        Long eventId
) {
}
