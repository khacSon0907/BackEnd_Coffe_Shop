package com.example.coffe_shop.auth.service;

import com.example.coffe_shop.auth.dto.*;

import com.example.coffe_shop.auth.model.User;
import com.example.coffe_shop.auth.model.UserPrincipal;
import com.example.coffe_shop.auth.repository.UserRepository;
import com.example.coffe_shop.response.ResponseMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailValidationService emailValidationService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisService otpRedisService;
    private final JwtService jwtService;

    //         refreshToken khi accen hết hạn
    public ResponseMessage<JwtResponse> refreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return new ResponseMessage<>(false, "Không tìm thấy cookie!", null);
        }

        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        if (refreshToken == null) {
            return new ResponseMessage<>(false, "Refresh token không có trong cookie!", null);
        }

        // Lấy email từ Redis theo refresh token
        String email = otpRedisService.getEmailFromRefreshToken(refreshToken);
        if (email == null) {
            return new ResponseMessage<>(false, "Token không hợp lệ hoặc đã hết hạn!", null);
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return new ResponseMessage<>(false, "Không tìm thấy người dùng!", null);
        }

        User user = userOptional.get();
        UserPrincipal userPrincipal = new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.isActive()
        );

        String newAccessToken = jwtService.generateAccessToken(userPrincipal);
        return new ResponseMessage<>(true, "Làm mới access token thành công!", new JwtResponse(newAccessToken));
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

        if (!emailValidationService.isValidEmail(request.getEmail())) {
            return new ResponseMessage<>(false, "Email không tồn tại hoặc không hợp lệ!", null);
        }

         emailService.sendOtpEmail(request.getEmail(), otp);
        otpRedisService.saveOtp(token, jsonData, 5);

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        return new ResponseMessage<>(true, "OTP đã gửi về email!",data);
    }

    public ResponseMessage<JwtResponse> login(LoginRequest request, HttpServletResponse response) {
        Optional<User> optional = userRepository.findByEmail(request.getEmail());

        if (optional.isEmpty()) {
            return new ResponseMessage<>(false, "Email không tồn tại!", null);
        }

        User user = optional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new ResponseMessage<>(false, "Mật khẩu không đúng!", null);
        }
        if (!user.isActive()) {
            return new ResponseMessage<>(false, "Tài khoản đã ngừng hoạt động!", null);
        }


        // Chuyển User → UserPrincipal
        UserPrincipal userPrincipal = new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.isActive()
        );

        String accessToken = jwtService.generateAccessToken(userPrincipal);
        String refreshToken = UUID.randomUUID().toString();
        otpRedisService.saveRefreshToken(request.getEmail(), refreshToken, 10080); // 7 ngày

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // dùng HTTPS mới bật cái này
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày

        response.addCookie(cookie);

        return new ResponseMessage<>(true, "Đăng nhập thành công", new JwtResponse(accessToken));
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
                    .active(true)
                    .createdAt(LocalDate.now())
                    .allowedIp("113.173.2.148")
                    .build();

            User saved = userRepository.save(user);
            otpRedisService.deleteOtp(request.getToken());

            return new ResponseMessage<>(true, "Xác minh OTP thành công! Tài khoản đã được tạo.", saved);
        } catch (Exception e) {
            return new ResponseMessage<>(false, "Lỗi xử lý dữ liệu OTP!", null);
        }
    }

}
