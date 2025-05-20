package com.example.coffe_shop.auth.controller;


import com.example.coffe_shop.auth.dto.*;
import com.example.coffe_shop.auth.model.User;
import com.example.coffe_shop.auth.service.AuthService;
import com.example.coffe_shop.response.ResponseMessage;
import com.example.coffe_shop.user.dto.ForgetPasswordRequest;
import com.example.coffe_shop.user.dto.ResetPasswordRequest;
import com.example.coffe_shop.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;


    // reshtoken khi acctoken hết hạn
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseMessage<JwtResponse>> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.refreshTokenFromCookie(request));
    }


    //Đăng ký tạo tài khoản user
    @PostMapping("/register")
    public ResponseEntity<ResponseMessage<Map<String, String>>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    //Gửi mã OTP tới email để confirm
    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseMessage<User>> verifyOtp(@RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ResponseMessage<Map<String, String>>> forgetPassword(
            @Valid @RequestBody ForgetPasswordRequest request) {
        return ResponseEntity.ok(userService.sendFogetPassWord(request));
    }

    @PostMapping("/verify-otpPassword")
    public ResponseEntity<ResponseMessage<String>> verifyOtpPassword(
            @Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(userService.verifyOtp(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseMessage<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetPassword(request));
    }




    @PostMapping("/login")
    public ResponseEntity<ResponseMessage<JwtResponse>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }




}
