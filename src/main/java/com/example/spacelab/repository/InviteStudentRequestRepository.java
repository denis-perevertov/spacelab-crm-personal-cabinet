package com.example.spacelab.repository;

import com.example.spacelab.model.student.StudentInviteRequest;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Hidden
@Repository
public interface InviteStudentRequestRepository extends JpaRepository<StudentInviteRequest, Long> {
    Optional<StudentInviteRequest> findByTokenLike(String token);
    void deleteByToken(String token);
}
