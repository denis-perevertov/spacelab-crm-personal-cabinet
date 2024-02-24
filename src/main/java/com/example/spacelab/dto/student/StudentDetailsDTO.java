package com.example.spacelab.dto.student;

import com.example.spacelab.model.student.StudentEducationLevel;
import com.example.spacelab.model.student.StudentEnglishLevel;
import com.example.spacelab.model.student.StudentWorkStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record StudentDetailsDTO(
//        @NotBlank(message = "validation.field.empty")
//        @Max(value = 25, message = "validation.field.length.max")
        String firstName,
//        @NotBlank(message = "validation.field.empty")
//        @Max(value = 25, message = "validation.field.length.max")
        String lastName,
//        @NotBlank
//        @Max(value = 25, message = "validation.field.length.max")
        String fathersName,
        @NotBlank(message = "validation.field.empty")
        @Pattern(regexp = "^(38)?0(99|50|66|97|96|98)\\d{7}$", message = "validation.field.format.allowed")
        String phone,
        @NotBlank(message = "validation.field.empty")
        @Email(message = "validation.field.format.allowed")
        String email,
        @Max(value = 25, message = "validation.field.length.max")
        String telegram,
        @Max(value = 100, message = "validation.field.length.max")
        String githubLink,
        @Max(value = 100, message = "validation.field.length.max")
        String linkedinLink,
        @NotNull(message = "validation.field.empty")
        LocalDate birthdate,
        @NotNull(message = "validation.field.select")
        StudentEducationLevel educationLevel,
        @NotNull(message = "validation.field.select")
        StudentEnglishLevel englishLevel,
        @NotNull(message = "validation.field.select")
        StudentWorkStatus workStatus
) {
}
