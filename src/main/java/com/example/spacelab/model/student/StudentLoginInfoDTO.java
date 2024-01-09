package com.example.spacelab.model.student;

import java.util.List;

public record StudentLoginInfoDTO(
        Long id,
        String fullName,
        String role,
        String avatar,
        List<Long> courses,
        List<String> permissions
) {
}
