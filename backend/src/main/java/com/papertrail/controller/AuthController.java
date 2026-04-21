package com.papertrail.controller;

import com.papertrail.dto.ApiResponse;
import com.papertrail.dto.AuthResponse;
import com.papertrail.dto.LoginRequest;
import com.papertrail.dto.OtpSendRequest;
import com.papertrail.dto.OtpVerifyRequest;
import com.papertrail.dto.RegisterRequest;
import com.papertrail.dto.UserProfileResponse;
import com.papertrail.security.AuthUserPrincipal;
import com.papertrail.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/profile")
    public UserProfileResponse profile(@AuthenticationPrincipal AuthUserPrincipal principal) {
        return authService.getProfile(principal.getUserId());
    }

    @PostMapping("/otp/send")
    public ResponseEntity<ApiResponse> sendOtp(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @Valid @RequestBody OtpSendRequest request
    ) {
        authService.sendOtp(principal.getUserId(), request);
        return ResponseEntity.ok(new ApiResponse(true, "OTP sent successfully"));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse> verifyOtp(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @Valid @RequestBody OtpVerifyRequest request
    ) {
        authService.verifyOtp(principal.getUserId(), request);
        return ResponseEntity.ok(new ApiResponse(true, "OTP verified successfully"));
    }
}
