package com.cloudvault.auth.security;

import com.cloudvault.auth.entity.Role;
import com.cloudvault.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Set test secret (256-bit Base64 encoded key) and expirations
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250655368566D5971");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 900000L); // 15 min
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800000L); // 7 days

        testUser = User.builder()
                .id(1L)
                .email("ahmet@cloudvault.com")
                .password("encodedPassword")
                .firstName("Ahmet")
                .lastName("Kısacık")
                .role(Role.ROLE_USER)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should generate valid JWT access token and extract username")
    void testGenerateTokenAndExtractUsername() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("ahmet@cloudvault.com", extractedUsername);
    }

    @Test
    @DisplayName("Should validate token against user details successfully")
    void testIsTokenValid() {
        String token = jwtService.generateToken(testUser);

        boolean isValid = jwtService.isTokenValid(token, testUser);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should generate valid refresh token")
    void testGenerateRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken(testUser);

        assertNotNull(refreshToken);
        assertEquals("ahmet@cloudvault.com", jwtService.extractUsername(refreshToken));
        assertTrue(jwtService.isTokenValid(refreshToken, testUser));
    }
}
