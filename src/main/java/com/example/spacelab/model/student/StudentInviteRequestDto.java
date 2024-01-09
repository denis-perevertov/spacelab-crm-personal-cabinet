package com.example.spacelab.model.student;

public record StudentInviteRequestDto(
        String firstName,
        String middleName,
        String lastName,
        String email,
        String phone,
        Long courseId
) {
}
