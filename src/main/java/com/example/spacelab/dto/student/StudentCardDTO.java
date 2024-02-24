package com.example.spacelab.dto.student;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StudentCardDTO {

    private String avatar;
    private StudentDetailsDTO studentDetails;
    @Schema(example = "RoleName")
    private String roleName;
    @Schema(example = "CourseName")
    private String courseName;
    private String courseIcon;


}
