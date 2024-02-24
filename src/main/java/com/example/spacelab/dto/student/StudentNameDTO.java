package com.example.spacelab.dto.student;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record StudentNameDTO(
        @NotBlank(message = "validation.field.empty")
        @Max(value = 25, message="validation.field.length.max")
        String firstName,
        @NotBlank(message = "validation.field.empty")
        @Max(value = 25, message="validation.field.length.max")
        String lastName,
        @NotBlank(message = "validation.field.empty")
        @Max(value = 25, message="validation.field.length.max")
        String fathersName
) {
}
