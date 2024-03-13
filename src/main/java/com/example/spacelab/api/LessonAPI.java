package com.example.spacelab.api;

import com.example.spacelab.dto.lesson.LessonInfoDTO;
import com.example.spacelab.dto.lesson.LessonListDTO;
import com.example.spacelab.dto.lesson.LessonReportRowSaveDTO;
import com.example.spacelab.dto.lesson.LessonSaveBeforeStartDTO;
import com.example.spacelab.dto.student.StudentLessonDisplayDTO;
import com.example.spacelab.util.FilterForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name="Lesson", description = "Lesson API")
public interface LessonAPI {

    @Operation(
            summary = "Get Student Lessons",
            description = "Get all lessons of the currently logged in student",
            responses = @ApiResponse(responseCode = "200", description = "Success", content = @Content(array = @ArraySchema(minItems = 2, schema = @Schema(implementation = LessonListDTO.class))))
    )
    ResponseEntity<?> getStudentLessons();

    @Operation(
            summary = "Get Lesson",
            description = "Get particular lesson by its ID",
            responses = @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = LessonInfoDTO.class)))
    )
    ResponseEntity<?> getLessonById(@Parameter(required = true, example = "1") Long id);

    @Operation(
            summary = "Get Student Lesson Data",
            description = "Get data of all students for lesson by its ID",
            responses = @ApiResponse(responseCode = "200", description = "Success", content = @Content(array = @ArraySchema(minItems = 2, schema = @Schema(implementation = StudentLessonDisplayDTO.class))))
    )
    ResponseEntity<?> getStudentLessonDisplayData(@Parameter(required = true, example = "1") Long id);
}
