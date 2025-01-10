package com.clerodri.web.dto.request;


import jakarta.validation.constraints.*;

import java.time.LocalDateTime;


public record RequestEventDTO(
        @NotBlank(message = "Event name is required")
        @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Name must contain only letters and spaces")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        String name,

        @NotBlank(message = "Description is required ")
        @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
        String description,

        @NotNull(message = "Date and time are required")
        @FutureOrPresent(message = "Date must be in the future or present")
        LocalDateTime date,

        @NotBlank(message = "Location is required ")
        @Size(min = 5, max=100, message = "Location must be between 5 and 100 characters")
        String location,

        @NotNull(message = "Capacity is required")
        @Positive
        int capacity
) {
}
