package com.example.spacelab.api;

import com.example.spacelab.dto.SelectDTO;
import com.example.spacelab.dto.course.*;
import com.example.spacelab.dto.task.TaskCourseDTO;
import com.example.spacelab.util.FilterForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;

@Tag(name = "Course", description = "Course API")
public interface CourseAPI {

    @Operation(
            summary = "Get Student Course",
            description = "Get student course data",
            responses = @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = CourseInfoPageDTO.class)))
    )
    ResponseEntity<?> getStudentCourseInfo();

    @Operation(
            summary = "Get Student Course Tasks",
            description = "Get student course tasks",
            responses = @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = StudentCourseTaskInfoDTO.class)))
    )
    ResponseEntity<?> getStudentCourseTasks();

    @Operation(
            summary = "Get Course Filter",
            description = "Get course filter options (?)",
            responses = @ApiResponse(responseCode = "200", description = "Success", content = @Content(array = @ArraySchema(minItems = 2, schema = @Schema(implementation = CourseSelectDTO.class))))
    )
    ResponseEntity<?> loadCourseFilter();

}
