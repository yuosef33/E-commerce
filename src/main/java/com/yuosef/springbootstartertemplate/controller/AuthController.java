package com.yuosef.springbootstartertemplate.controller;

import com.yuosef.springbootstartertemplate.models.Dtos.*;
import com.yuosef.springbootstartertemplate.services.Impl.EmailService;
import com.yuosef.springbootstartertemplate.services.Impl.OtpRedisService;
import com.yuosef.springbootstartertemplate.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.SystemException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Register, Login, Google-Oauth, Refresh Token, Logout")
public class AuthController {

    private final UserService userService;
    private final OtpRedisService otpRedisService;
    private final EmailService emailService;

    public AuthController(UserService userService, OtpRedisService otpRedisService, EmailService emailService) {
        this.userService = userService;
        this.otpRedisService = otpRedisService;
        this.emailService = emailService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/createUser")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody UserAccountInfo userAccountInfo) throws SystemException {
         UserDto user= userService.createUser(userAccountInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("User Created successfully",user));
    }

    @PostMapping("/Login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginInfo loginInfo){
        return ResponseEntity.ok(ApiResponse.ok("Login successful",userService.login(loginInfo)));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Token Refreshed",userService.refreshToken(request)));
    }

    @PostMapping("/createUserOtp")
    public ApiResponse<?> createUserOtp(@RequestBody UserAccountInfo request) throws SystemException {

        userService.checkUser(request);
        String otp = otpRedisService.generateOtp();
        PendingUser pendingUser = new PendingUser();
        pendingUser.setUser(request);
        pendingUser.setOtp(otp);

        otpRedisService.savePendingUser(request.email(), pendingUser);

        emailService.sendOtp(request.email(), otp);

        return ApiResponse.ok("OTP sent successfully");
    }
    @PostMapping("/verifyOtp")
    public ApiResponse<?> verifyOtp(@RequestBody VerifyOtpRequest request) {

        PendingUser pendingUser = otpRedisService.getPendingUser(request.email());

        if (pendingUser == null) {
            throw new IllegalArgumentException("OTP expired");
        }

        if (!pendingUser.getOtp().equals(request.otp())) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        userService.createUserOtp(pendingUser.getUser());

        otpRedisService.deletePendingUser(request.email());

        return ApiResponse.ok("User created successfully");
    }

    @GetMapping("/oauth2/callback")
    public ResponseEntity<ApiResponse<?>> callback(
            @RequestParam String code) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Code generated, call /auth/exchange-code before 60 seconds to get your tokens ",
                Map.of("code", code)
        ));
    }

    @PostMapping("/exchange-code")
    public ResponseEntity<ApiResponse<AuthResponse>> exchangeCode(@RequestParam String code) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Tokens generated successfully",
                userService.exchangeCode(code)
        ));
    }
}
