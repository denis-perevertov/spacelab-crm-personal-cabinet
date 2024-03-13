package com.example.spacelab.repository;

import com.example.spacelab.model.RefreshToken;
import com.example.spacelab.model.student.Student;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Hidden
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByPrincipal(Student student);

    @Query(
            """
            DELETE
            FROM RefreshToken rt
            WHERE rt.expiryDate <= :expiryDate
            """
    )
    @Modifying
    void deleteExpiredTokens(Instant expiryDate);
}
