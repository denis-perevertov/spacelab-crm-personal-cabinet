package com.example.spacelab.repository;

import com.example.spacelab.model.RefreshToken;
import com.example.spacelab.model.admin.Admin;
import com.example.spacelab.model.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByPrincipal(Student student);
}
