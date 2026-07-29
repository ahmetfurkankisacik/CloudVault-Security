package com.afk.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 503,
                "error", "Service Unavailable",
                "message", "Kimlik Doğrulama Servisi (auth-service) geçici olarak hizmet veremiyor. Lütfen tekrar deneyiniz."
        ));
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> userFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 503,
                "error", "Service Unavailable",
                "message", "Kullanıcı Servisi (user-service) geçici olarak hizmet veremiyor. Lütfen tekrar deneyiniz."
        ));
    }
}
