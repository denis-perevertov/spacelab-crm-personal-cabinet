package com.example.spacelab.controller;

import com.example.spacelab.api.AdminAPI;
import com.example.spacelab.dto.SelectSearchDTO;
import com.example.spacelab.dto.admin.AdminContactDTO;
import com.example.spacelab.dto.admin.AdminDTO;
import com.example.spacelab.dto.admin.AdminEditDTO;
import com.example.spacelab.exception.ErrorMessage;
import com.example.spacelab.exception.ObjectValidationException;
import com.example.spacelab.mapper.AdminMapper;
import com.example.spacelab.model.admin.Admin;
import com.example.spacelab.service.AdminService;
import com.example.spacelab.util.FilterForm;
import com.example.spacelab.validator.AdminValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admins")
public class AdminController implements AdminAPI {

    private final AdminService adminService;
    private final AdminMapper adminMapper;
    private final AdminValidator adminValidator;

    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<Page<AdminDTO>> getAdmins(@Parameter(name = "Filter object", description = "Collection of all filters for search results", required = false, example = "{}") FilterForm filters,
                                                    @RequestParam(required = false, defaultValue = "0") Integer page,
                                                    @RequestParam(required = false, defaultValue = "10") Integer size) {
        Page<AdminDTO> adminList;
        Page<Admin> adminPage;
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "id");
        adminPage = adminService.getAdmins(filters.trim(), pageable);
        adminList = new PageImpl<>(adminPage.stream().map(adminMapper::fromAdminToDTO).toList(), pageable, adminPage.getTotalElements());

        return new ResponseEntity<>(adminList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminDTO> getAdmin(@PathVariable @Parameter(example = "1") Long id) {
        Admin admin = adminService.getAdminById(id);
        return new ResponseEntity<>(adminMapper.fromAdminToDTO(admin), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AdminDTO> createNewAdmin(@RequestBody AdminEditDTO admin,
                                                    BindingResult bindingResult) {

        admin.setId(null);
        adminValidator.validate(admin, bindingResult);

        if(bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            throw new ObjectValidationException(errors);
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Admin savedAdmin = adminService.createAdmin(adminMapper.fromEditDTOToAdmin(admin));
        return new ResponseEntity<>(adminMapper.fromAdminToDTO(savedAdmin), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/edit")
    public ResponseEntity<AdminEditDTO> getAdminForEdit(@PathVariable Long id) {

        return new ResponseEntity<>(adminMapper.fromAdminToEditDTO(adminService.getAdminById(id)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminDTO> updateAdmin(@PathVariable @Parameter(example = "1") Long id,
                                                @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody AdminEditDTO admin,
                                                BindingResult bindingResult) {

        admin.setId(id);

        adminValidator.validate(admin, bindingResult);

        if(bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            throw new ObjectValidationException(errors);
        }

        if(admin.getPassword() != null && !admin.getPassword().isEmpty()) admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Admin savedAdmin = adminService.updateAdmin(adminMapper.fromEditDTOToAdmin(admin));
        return new ResponseEntity<>(adminMapper.fromAdminToDTO(savedAdmin), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable @Parameter(example = "1") Long id) {
        adminService.deleteAdminById(id);
        return new ResponseEntity<>("Admin with ID: " + id + " deleted", HttpStatus.OK);
    }

    // ==================================

    @GetMapping("/get-admins-by-role")
    public Map<String, Object> getAdminsByRole(@RequestParam(required=false) @Parameter(example = "1", name = "Role ID") Long roleID,
                                               @RequestParam(required=false) String roleName,
                                               @RequestParam @Parameter(example = "1") Integer page) {

        FilterForm form = FilterForm.with()
                                    .role(roleID)
                                    .build();
        Pageable pageable = PageRequest.of(page, 10);

        Page<Admin> adminPage =  adminService.getAdmins(form, pageable);

        List<SelectSearchDTO> adminList = adminPage.getContent()
                                                    .stream()
                                                    .map(admin -> new SelectSearchDTO(admin.getId(),
                                                            admin.getFirstName() + " " + admin.getLastName()))
                                                    .toList();
        Map<String, Object> selectMap = new HashMap<>();
        selectMap.put("results", adminList);
        selectMap.put("pagination", Map.of("more", adminPage.getNumber() < adminPage.getTotalPages()));

        return selectMap;
    }

    @GetMapping("/get-admins-list-by-role")
    public ResponseEntity<List<AdminContactDTO>> getAdminsListByRole(@RequestParam Long role) {
        FilterForm filters = FilterForm.with()
                .role(role)
                .build();
        List<AdminContactDTO> adminList = adminService.getAdmins(filters).stream()
                                                        .map(adminMapper::fromAdminToContactDTO)
                                                        .toList();
        return new ResponseEntity<>(adminList, HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAdminsWithoutCourses(FilterForm filters,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminService.getAdmins(filters, pageable).map(adminMapper::fromAdminToDTO));
    }
}
