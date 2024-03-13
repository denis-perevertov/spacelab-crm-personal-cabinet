package com.example.spacelab.service;

import com.example.spacelab.model.admin.Admin;
import com.example.spacelab.util.FilterForm;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Hidden
public interface AdminService extends EntityFilterService<Admin> {
    List<Admin> getAdmins();
    List<Admin> getAdmins(FilterForm filters);
    Page<Admin> getAdmins(Pageable pageable);
    Page<Admin> getAdmins(FilterForm filters, Pageable pageable);

    Admin getAdminById(Long id);
    Admin getAdminByEmail(String email);
    Admin createAdmin(Admin admin);
    Admin updateAdmin(Admin admin);

    void deleteAdminById(Long id);
}
