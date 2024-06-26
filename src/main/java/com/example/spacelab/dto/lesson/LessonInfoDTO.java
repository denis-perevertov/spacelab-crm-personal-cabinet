package com.example.spacelab.dto.lesson;

import com.example.spacelab.dto.admin.AdminAvatarDTO;
import com.example.spacelab.dto.course.CourseLinkIconDTO;
import com.example.spacelab.dto.student.StudentAvatarDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class LessonInfoDTO {

    @Schema(example = "10")
    private Long id;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime datetime;

    @Schema(example = "ACTIVE")
    private String status;

    private String link;

    private CourseLinkIconDTO course;

    private AdminAvatarDTO mentor;

    private List<StudentAvatarDTO> students;

    private List<LessonReportRowDTO> lessonReportRowList;
}
