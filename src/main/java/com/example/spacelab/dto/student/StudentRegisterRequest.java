package com.example.spacelab.dto.student;

import com.example.spacelab.model.student.StudentEducationLevel;
import com.example.spacelab.model.student.StudentEnglishLevel;
import com.example.spacelab.model.student.StudentWorkStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record StudentRegisterRequest(
        String firstName,
        String lastName,
        String middleName,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birthdate,
        String phone,
        String email,
        String telegram,
        String github,
        String linkedin,
        StudentEducationLevel education,
        StudentEnglishLevel english,
        StudentWorkStatus workStatus,
        String availableTime,

        String password,
        String confirmPassword,

        MultipartFile avatar,
        Long courseId
) {
}
