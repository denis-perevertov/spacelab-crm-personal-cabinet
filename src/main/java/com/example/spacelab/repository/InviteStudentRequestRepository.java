package com.example.spacelab.repository;

import com.example.spacelab.model.student.StudentInviteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InviteStudentRequestRepository extends JpaRepository<StudentInviteRequest, Long> {
    Optional<StudentInviteRequest> findByTokenLike(String token);
}
