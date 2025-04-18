package com.example.coffe_shop.auth.service;

import com.example.coffe_shop.auth.dto.*;

import com.example.coffe_shop.auth.model.User;
import com.example.coffe_shop.auth.repository.UserRepository;
import com.example.coffe_shop.response.ResponseMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisService otpRedisService;
    private final JwtService jwtService;


    // refreshToken khi accen hết hạn
    public ResponseMessage<JwtResponse> refreshToken(RefreshTokenRequest request) {
        String tokenSaved = otpRedisService.getRefreshToken(request.getEmail());
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (tokenSaved == null || !tokenSaved.equals(request.getRefreshToken())) {
            return new ResponseMessage<>(false, "Refresh token không hợp lệ hoặc đã hết hạn!", null);
        }

        if(userOptional.isEmpty()){
            return new ResponseMessage<>(false, "Email lỗi !", null);
        }

        String newAccessToken = jwtService.generateAccessToken(request.getEmail(),userOptional.get().getRole());
        return new ResponseMessage<>(true, "Làm mới accessToken thành công!", new JwtResponse(newAccessToken, request.getRefreshToken()));
    }


    // đăng ký tạo tài khoản user
    public ResponseMessage<Map<String, String>> register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new ResponseMessage<>(false, "Email đã tồn tại!", null);
        }

        // tạo OTP
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));

        // Tạo token
        String token = UUID.randomUUID().toString();

        //Tạo JSON chứa thông tin user + otp
        String jsonData = String.format("""
        {
          "email": "%s",
          "password": "%s",
          "fullname": "%s",
          "phoneNumber": "%s",
          "otp": "%s"
        }
        
        """, request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullname(),
                request.getPhoneNumber(),
                otp
        );

        try {
            //  Nếu gửi thất bại → ném lỗi
            emailService.sendOtpEmail(request.getEmail(), otp);
        } catch (Exception e) {
            return new ResponseMessage<>(false, "Không thể gửi email OTP. Vui lòng kiểm tra lại địa chỉ email!", null);
        }
        otpRedisService.saveOtp(token, jsonData, 5);

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        return new ResponseMessage<>(true, "OTP đã gửi về email!",data);
    }


    // đăng nhập
    public ResponseMessage<JwtResponse> login(LoginRequest request) {
        Optional<User> optional = userRepository.findByEmail(request.getEmail());

        if(optional.isEmpty()){
            return new ResponseMessage<>(false, " Email không tồn tại !", null);
        }

        if (!passwordEncoder.matches(request.getPassword(), optional.get().getPassword())) {
            return new ResponseMessage<>(false, " mật khẩu không đúng!", null);
        }

        User user = userRepository.findByEmail(request.getEmail()).get();
        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole());

        String refreshToken = UUID.randomUUID().toString();
        otpRedisService.saveRefreshToken(request.getEmail(), refreshToken, 10080); // 7 ngày


        // TODO: lưu refreshToken vào Redis nếu cần kiểm soát

        return new ResponseMessage<>(true, "Đăng nhập thành công", new JwtResponse(accessToken, refreshToken));
    }




    // gửi mã otp xác nhận đăng ký tài khoản
    public ResponseMessage<User> verifyOtp(VerifyOtpRequest request) {
        String json = otpRedisService.getOtp(request.getToken());

        if (json == null) {
            return new ResponseMessage<>(false, "Token không hợp lệ hoặc đã hết hạn!", null);
        }

        // Chuyển từ JSON về object bằng Jackson
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);

            String otpSaved = node.get("otp").asText();
            if (!otpSaved.equals(request.getOtp())) {
                return new ResponseMessage<>(false, "Mã OTP không đúng!", null);
            }

            // Tạo user từ dữ liệu trong JSON
            User user = User.builder()
                    .email(node.get("email").asText())
                    .password(node.get("password").asText())
                    .fullname(node.get("fullname").asText())
                    .phoneNumber(node.get("phoneNumber").asText())
                    .role("USER")
                    .build();

            User saved = userRepository.save(user);
            otpRedisService.deleteOtp(request.getToken());

            return new ResponseMessage<>(true, "Xác minh OTP thành công! Tài khoản đã được tạo.", saved);
        } catch (Exception e) {
            return new ResponseMessage<>(false, "Lỗi xử lý dữ liệu OTP!", null);
        }
    }

}
