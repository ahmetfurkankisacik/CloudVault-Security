package com.cloudvault.auth.service;

import com.cloudvault.auth.dto.*;
import com.cloudvault.auth.entity.Role;
import com.cloudvault.auth.entity.User;
import com.cloudvault.auth.exception.EmailAlreadyExistsException;
import com.cloudvault.auth.exception.InvalidTokenException;
import com.cloudvault.auth.repository.UserRepository;
import com.cloudvault.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("furkan@cloudvault.com")
                .password("encodedPassword")
                .firstName("Furkan")
                .lastName("Kısacık")
                .role(Role.ROLE_USER)
                .active(true)
                .build();

        registerRequest = RegisterRequest.builder()
                .email("furkan@cloudvault.com")
                .password("rawPassword123")
                .firstName("Furkan")
                .lastName("Kısacık")
                .build();

        loginRequest = LoginRequest.builder()
                .email("furkan@cloudvault.com")
                .password("rawPassword123")
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new user and return TokenResponse")
    void testRegisterSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("access_token_xyz");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh_token_xyz");
        when(jwtService.getJwtExpiration()).thenReturn(900000L);

        TokenResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("access_token_xyz", response.getAccessToken());
        assertEquals("refresh_token_xyz", response.getRefreshToken());
        assertEquals("furkan@cloudvault.com", response.getUser().getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when registering existing email")
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully authenticate user and return TokenResponse")
    void testLoginSuccess() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser)).thenReturn("access_token_xyz");
        when(jwtService.generateRefreshToken(testUser)).thenReturn("refresh_token_xyz");
        when(jwtService.getJwtExpiration()).thenReturn(900000L);

        TokenResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("access_token_xyz", response.getAccessToken());
        assertEquals("furkan@cloudvault.com", response.getUser().getEmail());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on invalid login password")
    void testLoginBadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Should validate token successfully")
    void testValidateTokenSuccess() {
        String token = "Bearer valid_token_string";
        when(jwtService.extractUsername("valid_token_string")).thenReturn("furkan@cloudvault.com");
        when(userRepository.findByEmail("furkan@cloudvault.com")).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid("valid_token_string", testUser)).thenReturn(true);

        UserResponse userResponse = authService.validateToken(token);

        assertNotNull(userResponse);
        assertEquals("furkan@cloudvault.com", userResponse.getEmail());
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when token is expired or invalid")
    void testValidateTokenInvalid() {
        String token = "Bearer invalid_token_string";
        when(jwtService.extractUsername("invalid_token_string")).thenReturn("furkan@cloudvault.com");
        when(userRepository.findByEmail("furkan@cloudvault.com")).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid("invalid_token_string", testUser)).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> authService.validateToken(token));
    }
}
