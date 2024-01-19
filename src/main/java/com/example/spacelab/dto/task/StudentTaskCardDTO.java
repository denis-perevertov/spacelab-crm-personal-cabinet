package com.example.spacelab.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudentTaskCardDTO(
        Long id,
        @JsonFormat(pattern = "dd.MM.yyyy")
        LocalDate startDate,
        @JsonFormat(pattern = "dd.MM.yyyy")
        LocalDate endDate,
        TaskInfoDTO taskReference,
        StudentTaskLinkDTO parentStudentTask,
        String taskListId,
        String projectId
) {
}
