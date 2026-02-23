package com.uds.ged.application.service;

import com.uds.ged.application.dto.request.LoginRequest;
import com.uds.ged.application.dto.request.RegisterRequest;
import com.uds.ged.application.dto.response.AuthResponse;
import com.uds.ged.infrastructure.exception.EmailAlreadyExistsException;
import com.uds.ged.infrastructure.exception.PasswordMismatchException;
import com.uds.ged.infrastructure.exception.UsernameAlreadyExistsException;
import com.uds.ged.domain.model.User;
import com.uds.ged.domain.model.enums.UserRole;
import com.uds.ged.domain.repository.UserRepository;
import com.uds.ged.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.debug("Attempting login for user: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User {} logged in successfully", request.getUsername());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Registers a new user in the system.
     * Validates password match, username and email uniqueness.
     * Automatically authenticates the user after successful registration.
     *
     * @param request the registration request containing user details
     * @return authentication response with JWT token
     * @throws PasswordMismatchException if passwords don't match
     * @throws UsernameAlreadyExistsException if username is already taken
     * @throws EmailAlreadyExistsException if email is already registered
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt - username: {}, email: {}", 
                request.getUsername(), request.getEmail());

        // Validar se as senhas coincidem
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Registration failed - password mismatch for username: {}", request.getUsername());
            throw new PasswordMismatchException();
        }

        // Validar se o username já existe
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed - username already exists: {}", request.getUsername());
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        // Validar se o email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // Criar novo usuário
        UserRole assignedRole = request.getRole() != null ? request.getRole() : UserRole.USER;
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(assignedRole)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully - username: {}, email: {}, role: {}", 
                user.getUsername(), user.getEmail(), user.getRole());

        // Autenticar automaticamente após registro
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        
        log.debug("JWT token generated for user: {}", user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Checks if a username is available for registration.
     *
     * @param username the username to check
     * @return true if available, false if already taken
     */
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * Checks if an email is available for registration.
     *
     * @param email the email to check
     * @return true if available, false if already registered
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}
