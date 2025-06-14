package com.whatchat.backend.controller;

import com.whatchat.backend.dto.auth.AuthResponse;
import com.whatchat.backend.dto.auth.LoginRequest;
import com.whatchat.backend.dto.auth.RegisterRequest;
import com.whatchat.backend.entity.User;
import com.whatchat.backend.payload.response.ApiResponse;
import com.whatchat.backend.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
         authService.register(request);
        ApiResponse<Void> response = new ApiResponse<>(true, "user registered successfully", null,null);
        return ResponseEntity.ok(response);
        // TODO:  exception handler ekle, hataları da düzenli biçimde yakala
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse data= authService.login(request);
        ApiResponse<AuthResponse> response = new ApiResponse<>(true,"Login Successful",null,data);
        return ResponseEntity.ok(response);

    }


}