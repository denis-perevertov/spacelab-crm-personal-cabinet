package com.example.spacelab.controller;

import com.example.spacelab.dto.GenericResponseObject;
import com.example.spacelab.dto.SelectDTO;
import com.example.spacelab.dto.course.CourseCardDTO;
import com.example.spacelab.dto.course.CourseSelectDTO;
import com.example.spacelab.dto.literature.*;
import com.example.spacelab.dto.student.StudentDTO;
import com.example.spacelab.exception.ErrorMessage;
import com.example.spacelab.exception.ObjectValidationException;
import com.example.spacelab.mapper.LiteratureMapper;
import com.example.spacelab.model.admin.Admin;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.literature.Literature;
import com.example.spacelab.model.literature.LiteratureType;
import com.example.spacelab.model.role.PermissionType;
import com.example.spacelab.model.student.StudentAccountStatus;
import com.example.spacelab.service.LiteratureService;
import com.example.spacelab.util.AuthUtil;
import com.example.spacelab.util.FilterForm;
import com.example.spacelab.validator.LiteratureValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Tag(name="Literature", description = "Literature controller")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/literature")
public class LiteratureController {

    private final LiteratureService literatureService;
    private final LiteratureMapper mapper;
    private final LiteratureValidator validator;

    // Получение списка литературы с фильтрацией и пагинацией
    @GetMapping
    public ResponseEntity<Page<LiteratureCardDTO>> getLiterature(@Parameter(name = "Filter object", description = "Collection of all filters for search results", example = "{}") FilterForm filters,
                                                                 @RequestParam(required = false, defaultValue = "0") Integer page,
                                                                 @RequestParam(required = false, defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");

        Course studentCourse = AuthUtil.getLoggedInPrincipal().getCourse();
        filters.setCourse(studentCourse.getId());

        return new ResponseEntity<>(literatureService.getLiterature(filters, pageable).map(mapper::fromLiteratureToCardDTO), HttpStatus.OK);
    }

    // Получение литературы по id
    @GetMapping("/{id}")
    public ResponseEntity<LiteratureInfoDTO> getLiteratureById(@PathVariable @Parameter(example = "1") Long id) {

        // todo validation -> literature belongs to student course

        LiteratureInfoDTO lit = mapper.fromLiteraturetoInfoDTO(literatureService.getLiteratureById(id));
        return new ResponseEntity<>(lit, HttpStatus.OK);
    }

    // Создание новой литературы
    @PostMapping
    public ResponseEntity<?> createNewLiterature( @ModelAttribute LiteratureSaveDTO dto,
                                                  BindingResult bindingResult) {

        dto.setId(null);

        validator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            throw new ObjectValidationException(errors);
        }

        try {
            Literature createdLiterature = literatureService.createNewLiterature(dto);
            return new ResponseEntity<>(mapper.fromLiteratureToListDTO(createdLiterature), HttpStatus.CREATED);
        } catch (IOException e) {
            log.error("Exception with saving lit file: " + e.getMessage());
            return ResponseEntity.badRequest().body("Exception with saving file!");
        } catch (Exception e) {
            log.error("Unknown exception during lit creation: " + e.getMessage());
            return ResponseEntity.badRequest().body("Could not save literature!");
        }
    }

    // Скачать литературу
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadLiteratureResource(@PathVariable Long id) throws IOException {
        File file = literatureService.getLiteratureFileById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(file.getName(), file.getName());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        log.info("Finished with response");
        return new ResponseEntity<>(new InputStreamResource(new FileInputStream(file)), headers, HttpStatus.OK);
    }

    // Получение списка типов литературы
    @GetMapping("/get-literature-type-list")
    public List<SelectDTO> getStatusList() {
        return Arrays.stream(LiteratureType.values()).map(type -> new SelectDTO(type.name(), type.name())).toList();
    }

}
