package com.clerodri.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RequestUserDTO(

         @NotBlank
         @Pattern(regexp = "^[A-Za-z\\s]+$", message = "USERNAME must contain only letters")
         @Size(max = 20)
         String username,
         @NotBlank
         String password,
         @Email
         @NotBlank
         String email
) {
}
