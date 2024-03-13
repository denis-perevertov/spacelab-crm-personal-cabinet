package com.example.spacelab.service.impl;

import com.example.spacelab.exception.ResourceNotFoundException;
import com.example.spacelab.model.RefreshToken;
import com.example.spacelab.model.student.Student;
import com.example.spacelab.repository.RefreshTokenRepository;
import com.example.spacelab.repository.StudentRepository;
import com.example.spacelab.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final StudentRepository studentRepository;

    @Override
    public RefreshToken findByToken(String token) {
        return repository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("Refresh token not found!"));
    }

    @Override
    public RefreshToken createRefreshToken(String username) {
        log.info("Creating refresh token for username: {}", username);
        Student student = studentRepository.findByDetailsEmail(username).orElseThrow(() -> new ResourceNotFoundException("Student not found!"));
        Optional<RefreshToken> opt = repository.findByPrincipal(student);
        RefreshToken token = opt.orElseGet(RefreshToken::new);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(Duration.ofDays(7)));
        token.setPrincipal(student);
        return repository.save(token);
    }

    @Scheduled(fixedRate = 1000 * 60) // every 1 min
    @Transactional
    public void deleteExpiredTokens() {
        repository.deleteExpiredTokens(Instant.now());
    }
}
