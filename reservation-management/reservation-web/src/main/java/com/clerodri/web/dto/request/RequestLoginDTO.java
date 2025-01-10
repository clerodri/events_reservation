package com.clerodri.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RequestLoginDTO(

        String username,
        String password
) {
}
