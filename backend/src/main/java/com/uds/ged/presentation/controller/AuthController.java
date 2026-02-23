package com.uds.ged.presentation.controller;

import com.uds.ged.application.dto.request.LoginRequest;
import com.uds.ged.application.dto.request.RegisterRequest;
import com.uds.ged.application.dto.response.ApiResponse;
import com.uds.ged.application.dto.response.AuthResponse;
import com.uds.ged.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User registered successfully"));
    }

    /**
     * Checks if a username is available for registration.
     *
     * @param username the username to check
     * @return true if available, false if already taken
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsernameAvailability(@PathVariable String username) {
        boolean isAvailable = authService.isUsernameAvailable(username);
        return ResponseEntity.ok(isAvailable);
    }

    /**
     * Checks if an email is available for registration.
     *
     * @param email the email to check
     * @return true if available, false if already registered
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailAvailability(@PathVariable String email) {
        boolean isAvailable = authService.isEmailAvailable(email);
        return ResponseEntity.ok(isAvailable);
    }
}
