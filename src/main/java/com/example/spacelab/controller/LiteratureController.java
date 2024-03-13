package com.example.spacelab.controller;

import com.example.spacelab.dto.SelectDTO;
import com.example.spacelab.dto.literature.LiteratureCardDTO;
import com.example.spacelab.dto.literature.LiteratureInfoDTO;
import com.example.spacelab.dto.literature.LiteratureSaveDTO;
import com.example.spacelab.exception.ObjectValidationException;
import com.example.spacelab.mapper.LiteratureMapper;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.literature.Literature;
import com.example.spacelab.model.literature.LiteratureType;
import com.example.spacelab.service.LiteratureService;
import com.example.spacelab.util.AuthUtil;
import com.example.spacelab.util.FilterForm;
import com.example.spacelab.validator.LiteratureValidator;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        return new ResponseEntity<>(literatureService.getLiterature(filters.trim(), pageable).map(mapper::fromLiteratureToCardDTO), HttpStatus.OK);
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
        return new ResponseEntity<>(new InputStreamResource(new FileInputStream(file)), headers, HttpStatus.OK);
    }

    // Получение списка типов литературы
    @GetMapping("/types")
    public List<SelectDTO> getStatusList() {
        return Arrays.stream(LiteratureType.values()).map(type -> new SelectDTO(type.name(), type.name())).toList();
    }

}
