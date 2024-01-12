package com.example.spacelab.dto.task;

public record StudentTaskCardDTO(
        Long id,
        TaskInfoDTO taskReference,
        StudentTaskLinkDTO parentStudentTask
) {
}
