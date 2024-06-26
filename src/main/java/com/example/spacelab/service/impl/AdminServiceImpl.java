package com.example.spacelab.service.impl;

import com.example.spacelab.exception.ResourceNotFoundException;
import com.example.spacelab.model.admin.Admin;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.role.UserRole;
import com.example.spacelab.repository.AdminRepository;
import com.example.spacelab.repository.CourseRepository;
import com.example.spacelab.repository.UserRoleRepository;
import com.example.spacelab.service.AdminService;
import com.example.spacelab.service.specification.AdminSpecifications;
import com.example.spacelab.util.FilterForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRoleRepository userRoleRepository;
    private final CourseRepository courseRepository;

    @Override
    public List<Admin> getAdmins() {
        log.info("Getting admins without filtering or pages");
        return adminRepository.findAll();
    }

    @Override
    public List<Admin> getAdmins(FilterForm filters) {
        Specification<Admin> spec = buildSpecificationFromFilters(filters);
        return adminRepository.findAll(spec);
    }

    @Override
    public Page<Admin> getAdmins(Pageable pageable) {
        log.info("Getting admins with page " + pageable.getPageNumber() + "/ size " + pageable
                .getPageSize());
        return adminRepository.findAll(pageable);
    }

    @Override
    public Page<Admin> getAdmins(FilterForm filters, Pageable pageable) {
        log.info("Getting admins with page " + pageable.getPageNumber() + "/ size " + pageable
                .getPageSize() + " and filters: " + filters);
        Specification<Admin> spec = buildSpecificationFromFilters(filters);
        return adminRepository.findAll(spec,pageable);
    }

    @Override
    public Admin getAdminById(Long id) {
        log.info("Getting admin with ID: " + id);
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Admin not found!", Admin.class));
        return admin;
    }

    @Override
    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email).orElseThrow();
    }

    @Override
    public Admin createAdmin(Admin admin) {
        log.info("Creating new admin");
        try {
            admin = adminRepository.save(admin);
            log.info("Saved admin: " + admin);
            return admin;
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new RuntimeException("Unknown error during saving");
        }
    }

    @Override
    public Admin updateAdmin(Admin admin) {
        log.info("Updating admin");
        try {
            admin = adminRepository.save(admin);
            log.info("Saved admin: " + admin);
            return admin;
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new RuntimeException("Unknown error during saving");
        }
    }

    @Override
    public void deleteAdminById(Long id) {
        Admin admin = adminRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Admin not found!", Admin.class));
        log.info("Deleting admin with ID: " + id);
        adminRepository.delete(admin);
    }

    @Override
    public Specification<Admin> buildSpecificationFromFilters(FilterForm filters) {

        log.info("Building specification from filters: " + filters);

        Long id = filters.getId();

        String combined = filters.getCombined();

        Long roleID = filters.getRole();
        Long courseID = filters.getCourse();
        String phone = filters.getPhone();

        UserRole role = (roleID == null) ? null : userRoleRepository.getReferenceById(roleID);
        Course course = (courseID == null) ? null : courseRepository.getReferenceById(courseID);

        if(course != null) log.info(course.toString());

        Specification<Admin> combinedSpec = AdminSpecifications.hasNameLike(combined)
                                            .or(AdminSpecifications.hasEmailLike(combined));

        Specification<Admin> spec = Specification.where(combinedSpec)
                                    .and(AdminSpecifications.hasId(id))
                                    .and(AdminSpecifications.hasRole(role))
                                    .and(AdminSpecifications.hasCourse(course))
                                    .and(AdminSpecifications.hasPhoneLike(phone));

        return spec;
    }

}
