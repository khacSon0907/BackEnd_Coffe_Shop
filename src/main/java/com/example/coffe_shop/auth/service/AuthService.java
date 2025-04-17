package com.example.coffe_shop.auth.service;

import com.example.coffe_shop.auth.dto.LoginRequest;
import com.example.coffe_shop.auth.dto.RegisterRequest;
import com.example.coffe_shop.auth.model.User;
import com.example.coffe_shop.auth.repository.UserRepository;
import com.example.coffe_shop.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    // đăng ký tạo tài khoản user
    public ResponseMessage<User> register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new ResponseMessage<>(false, "Email đã tồn tại!", null);
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // 👈 mã hóa tại đây
                .fullname(request.getFullname())
                .phoneNumber(request.getPhoneNumber())
                .build();
        User saved = userRepository.save(user);
        return new ResponseMessage<>(true, "Đăng ký thành công!", saved);
    }


    public ResponseMessage<User> login(LoginRequest request) {
        Optional<User> optional = userRepository.findByEmail(request.getEmail());

        if (optional.isEmpty()) {
            return new ResponseMessage<>(false, "Email không tồn tại", null);
        }

        User user = optional.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new ResponseMessage<>(false, "Sai mật khẩu", null);
        }



        user.setPassword(null); // Ẩn password
        return new ResponseMessage<>(true, "Đăng nhập thành công!", user);
    }
}
