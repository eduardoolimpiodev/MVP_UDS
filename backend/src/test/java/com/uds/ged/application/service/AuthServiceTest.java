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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        loginRequest = new LoginRequest("testuser", "password123");
        
        registerRequest = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();
    }

    @Test
    @DisplayName("Should successfully authenticate user and return JWT token")
    void shouldAuthenticateUserSuccessfully() {
        Authentication authentication = mock(Authentication.class);
        String expectedToken = "jwt.token.here";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(expectedToken);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(expectedToken);
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRole()).isEqualTo("USER");

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider, times(1)).generateToken(authentication);
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should return correct role for admin user")
    void shouldReturnAdminRole() {
        testUser.setRole(UserRole.ADMIN);
        Authentication authentication = mock(Authentication.class);
        String expectedToken = "jwt.token.here";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(expectedToken);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getRole()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should successfully register new user")
    void shouldRegisterUserSuccessfully() {
        Authentication authentication = mock(Authentication.class);
        String expectedToken = "jwt.token.here";
        User savedUser = User.builder()
                .id(1L)
                .username("newuser")
                .email("newuser@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(expectedToken);

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(expectedToken);
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUsername()).isEqualTo("newuser");
        assertThat(response.getEmail()).isEqualTo("newuser@example.com");
        assertThat(response.getRole()).isEqualTo("USER");

        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw PasswordMismatchException when passwords don't match")
    void shouldThrowPasswordMismatchException() {
        registerRequest.setConfirmPassword("differentPassword");

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(PasswordMismatchException.class)
                .hasMessageContaining("do not match");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UsernameAlreadyExistsException when username exists")
    void shouldThrowUsernameAlreadyExistsException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("newuser");

        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email exists")
    void shouldThrowEmailAlreadyExistsException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("newuser@example.com");

        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should register user with ADMIN role when specified")
    void shouldRegisterUserWithAdminRole() {
        registerRequest.setRole(UserRole.ADMIN);
        Authentication authentication = mock(Authentication.class);
        String expectedToken = "jwt.token.here";
        User savedUser = User.builder()
                .id(1L)
                .username("newuser")
                .email("newuser@example.com")
                .password("encodedPassword")
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(expectedToken);

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getRole()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should encode password before saving")
    void shouldEncodePasswordBeforeSaving() {
        Authentication authentication = mock(Authentication.class);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("token");

        authService.register(registerRequest);

        verify(passwordEncoder, times(1)).encode("password123");
    }
}
