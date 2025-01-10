package com.clerodri.web.dto.response;

public record ResponseEventDTO(
        Long id,
        String name,
        String description,
        String date,
        String location,
        int capacity,
        int availability
) {
}
