package com.example.spacelab.dto.course;

import lombok.Data;

@Data
public class CourseEditDTO {

    private Long id;
    private String name;
    private CourseInfoDTO info;
    private CourseMembersDTO members;
    private CourseTaskStructureDTO structure;
}
