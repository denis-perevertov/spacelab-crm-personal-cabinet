package com.example.spacelab.repository;

import com.example.spacelab.model.role.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    UserRole findByName(String name);
    boolean existsByName(String name);
    UserRole getReferenceByName(String name);
}
