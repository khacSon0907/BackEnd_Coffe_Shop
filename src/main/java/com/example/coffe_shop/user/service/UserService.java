package com.example.coffe_shop.user.service;


import com.example.coffe_shop.auth.dto.VerifyOtpRequest;
import com.example.coffe_shop.auth.model.User;
import com.example.coffe_shop.auth.repository.UserRepository;
import com.example.coffe_shop.auth.service.EmailService;
import com.example.coffe_shop.auth.service.RedisService;
import com.example.coffe_shop.response.ResponseMessage;
import com.example.coffe_shop.user.dto.ChangePasswordRequest;
import com.example.coffe_shop.user.dto.ForgetPasswordRequest;
import com.example.coffe_shop.user.dto.ResetPasswordRequest;
import com.example.coffe_shop.user.dto.UpdateUserRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService otpRedisService;
    private final EmailService emailService;

    public ResponseMessage<Map<String,String>> sendFogetPassWord(ForgetPasswordRequest request){
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if(optionalUser.isEmpty()){
            return new ResponseMessage<>(false, "Email không tồn tại!", null);
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        String token = UUID.randomUUID().toString();
        String jsonData = String.format("""
        {
          "email": "%s",
          "otp": "%s"
        }
        """, request.getEmail(), otp);
        otpRedisService.saveOtp(token, jsonData, 5); // TTL 5 phút
        emailService.sendOtpForgetPassWord(request.getEmail(), otp);

        Map<String, String> data = new HashMap<>();
        data.put("token", token);

        return new ResponseMessage<>(true, "OTP đã gửi về email!", data);
    }


    public ResponseMessage<String> verifyOtp(VerifyOtpRequest request) {
        String json = otpRedisService.getOtp(request.getToken());
        if (json == null) {
            return new ResponseMessage<>(false, "Token không hợp lệ hoặc đã hết hạn!", null);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);

            String otpSaved = node.get("otp").asText();
            if (!otpSaved.equals(request.getOtp())) {
                return new ResponseMessage<>(false, "Mã OTP không đúng!", null);
            }

            return new ResponseMessage<>(true, "Xác thực OTP thành công!", null);
        } catch (Exception e) {
            return new ResponseMessage<>(false, "Lỗi xử lý OTP!", null);
        }
    }


    public ResponseMessage<String> resetPassword(ResetPasswordRequest request) {
        String json = otpRedisService.getOtp(request.getToken());
        if (json == null) {
            return new ResponseMessage<>(false, "Token không hợp lệ hoặc đã hết hạn!", null);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);
            String email = node.get("email").asText();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy email trong hệ thống"));

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            otpRedisService.deleteOtp(request.getToken());

            return new ResponseMessage<>(true, "Đặt lại mật khẩu thành công!", null);

        } catch (Exception e) {
            return new ResponseMessage<>(false, "Lỗi xử lý dữ liệu OTP!", null);
        }
    }


    public ResponseMessage<List<User>> getAllUser(){
        List<User> userList = userRepository.findAll();
        return new ResponseMessage<>(true,"Danh sách user ",userList);
    }

    public ResponseMessage<Optional<User>> getUserById(String id){
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()){
            return  new ResponseMessage<>(false,"Không tìm thấy id User " ,null);
        }

        return new ResponseMessage<>(true,"Succes find by idUser" , optionalUser);
    }


    public ResponseMessage<String> deleteUserbyId(String id){
        Optional<User> userOptional = userRepository.findById(id);

        if(userOptional.isPresent()){
            userRepository.deleteById(id);
            return  new ResponseMessage<>(true,"Xoá thành công user : " + userOptional.get().getFullname(),null);
        }
        else  {
            return  new ResponseMessage<>(false,"Id user không tồn tại  : " + id,null);
        }
    }

    public ResponseMessage<String> changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy email user"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new ResponseMessage<>(false, "Mật khẩu cũ không đúng!", null);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            return new ResponseMessage<>(false, "Mật khẩu mới không được trùng mật khẩu cũ!", null);
        }


        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new ResponseMessage<>(true, "Đổi mật khẩu thành công", null);
    }
    public ResponseMessage<User> updateUser (String email, UpdateUserRequest request){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            return new ResponseMessage<>(false,"không tim thấy email user",null);
        }
        User user = optionalUser.get();
        user.setFullname(request.getFullname());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setAddress(request.getAddress());
        userRepository.save(user);
        return  new ResponseMessage<>(true,"Update User thành công ", user);
    }

    public ResponseMessage<String> forgetPassword(String email ){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return new ResponseMessage<>(true,"Hãy lưu lại mật khẩu " , null);
    }
}
