package com.cloudvault.auth.controller;

import com.cloudvault.auth.dto.*;
import com.cloudvault.auth.entity.Role;
import com.cloudvault.auth.exception.EmailAlreadyExistsException;
import com.cloudvault.auth.exception.GlobalExceptionHandler;
import com.cloudvault.auth.security.JwtService;
import com.cloudvault.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private TokenResponse mockTokenResponse;
    private UserResponse mockUserResponse;

    @BeforeEach
    void setUp() {
        mockUserResponse = UserResponse.builder()
                .id(1L)
                .email("ahmet@cloudvault.com")
                .firstName("Ahmet")
                .lastName("Kısacık")
                .role(Role.ROLE_USER)
                .active(true)
                .build();

        mockTokenResponse = TokenResponse.builder()
                .accessToken("mock_access_token_123")
                .refreshToken("mock_refresh_token_456")
                .tokenType("Bearer")
                .expiresIn(900L)
                .user(mockUserResponse)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Should return 201 Created and TokenResponse")
    void testRegisterSuccess() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("ahmet@cloudvault.com")
                .password("Password123!")
                .firstName("Ahmet")
                .lastName("Kısacık")
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(mockTokenResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("mock_access_token_123"))
                .andExpect(jsonPath("$.refreshToken").value("mock_refresh_token_456"))
                .andExpect(jsonPath("$.user.email").value("ahmet@cloudvault.com"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Should return 409 Conflict when email already exists")
    void testRegisterEmailConflict() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("duplicate@cloudvault.com")
                .password("Password123!")
                .firstName("Ahmet")
                .lastName("Kısacık")
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already registered"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Should return 200 OK and TokenResponse")
    void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("ahmet@cloudvault.com")
                .password("Password123!")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(mockTokenResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock_access_token_123"))
                .andExpect(jsonPath("$.user.email").value("ahmet@cloudvault.com"));
    }

    @Test
    @DisplayName("GET /api/v1/auth/validate - Should return 200 OK and UserResponse for valid token")
    void testValidateTokenSuccess() throws Exception {
        String token = "Bearer mock_access_token_123";
        when(authService.validateToken(token)).thenReturn(mockUserResponse);

        mockMvc.perform(get("/api/v1/auth/validate")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ahmet@cloudvault.com"))
                .andExpect(jsonPath("$.firstName").value("Ahmet"));
    }
}
