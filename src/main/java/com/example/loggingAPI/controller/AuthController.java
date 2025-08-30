package com.example.loggingAPI.controller;

import com.example.loggingAPI.config.JwtUtil;
import com.example.loggingAPI.dto.AuthRequest;
import com.example.loggingAPI.model.Users;
import com.example.loggingAPI.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        userService.register(request.getUsername(), request.getPassword());
        log.info("User registered: {}", request.getUsername());
        return ResponseEntity.ok("Registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        Users users = userService.authenticate(request.getUsername(), request.getPassword());
        String token = jwtUtil.generateToken(users.getUsername());
        log.info("Login successful: {}", request.getUsername());
        return ResponseEntity.ok(token);
    }
}
