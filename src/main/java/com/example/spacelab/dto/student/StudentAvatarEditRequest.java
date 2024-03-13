package com.example.spacelab.dto.student;

import org.springframework.web.multipart.MultipartFile;

public record StudentAvatarEditRequest(
        MultipartFile avatar
) {
}
