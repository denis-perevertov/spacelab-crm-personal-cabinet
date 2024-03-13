package com.example.spacelab.controller.auth;

import com.example.spacelab.api.AuthAPI;
import com.example.spacelab.config.JwtService;
import com.example.spacelab.dto.student.StudentRegisterRequest;
import com.example.spacelab.exception.TokenException;
import com.example.spacelab.mapper.StudentMapper;
import com.example.spacelab.model.RefreshToken;
import com.example.spacelab.model.student.Student;
import com.example.spacelab.model.student.StudentInviteRequest;
import com.example.spacelab.model.student.StudentInviteRequestDto;
import com.example.spacelab.repository.InviteStudentRequestRepository;
import com.example.spacelab.repository.StudentRepository;
import com.example.spacelab.service.RefreshTokenService;
import com.example.spacelab.service.StudentService;
import com.example.spacelab.util.AuthRequest;
import com.example.spacelab.util.AuthResponse;
import com.example.spacelab.util.RefreshTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthAPI {

    private final JwtService jwtService;
    private final StudentService studentService;
    private final StudentMapper studentMapper;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;

    private final InviteStudentRequestRepository inviteStudentRequestRepository;
    private final StudentRepository studentRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        log.info("LOGIN METHOD!");
        log.info(authRequest.toString());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if(authentication.isAuthenticated()) {
                log.info("is authenticated!");
                String access_token = jwtService.generateToken(authRequest.username());
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.username());
                return ResponseEntity.ok(new AuthResponse(access_token, refreshToken.getToken()));
            }
            else {
                log.error("Incorrect auth request!");
                throw new BadCredentialsException("Incorrect authentication request");
            }
        } catch (BadCredentialsException ex) {
            log.error("bad credentials!");
            throw new BadCredentialsException("Incorrect authentication request");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refresh_token) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refresh_token.refresh_token());
        if(refreshToken.getExpiryDate().isAfter(Instant.now())) {
            String newAccessToken = jwtService.generateToken(refreshToken.getPrincipal().getDetails().getEmail());
            return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken.getToken()));
        }
        else throw new TokenException("Refresh token expired!");
    }

    @PostMapping("/invite-data")
    public ResponseEntity<?> getDataFromInviteLink(@RequestBody String token) {
        log.info("Getting invite data from token {}", token);
        token = token.replaceAll("\"", "").trim();
        StudentInviteRequest inviteData = inviteStudentRequestRepository.findByTokenLike(token)
                .orElseThrow(() -> new EntityNotFoundException("Invite data is not found by this token!"));
        StudentInviteRequestDto dto = new StudentInviteRequestDto(
                inviteData.getFirstName(),
                inviteData.getFathersName(),
                inviteData.getLastName(),
                inviteData.getEmail(),
                inviteData.getPhone(),
                inviteData.getCourse() != null ? inviteData.getCourse().getId() : -1
        );
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@ModelAttribute StudentRegisterRequest request) throws IOException {
        log.info("Student register data: {}", request);
        Student registeredStudent = studentService.registerStudent(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthData(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String email = jwtService.extractUsername(token);
        return ResponseEntity.ok(studentMapper.fromStudentToLoginInfoDTO(studentRepository.findByDetailsEmail(email).orElseThrow()));
    }

}
