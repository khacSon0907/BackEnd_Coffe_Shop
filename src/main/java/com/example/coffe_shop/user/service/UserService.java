package com.example.coffe_shop.user.service;


import com.example.coffe_shop.auth.model.User;
import com.example.coffe_shop.auth.repository.UserRepository;
import com.example.coffe_shop.response.ResponseMessage;
import com.example.coffe_shop.user.dto.ChangePasswordRequest;
import com.example.coffe_shop.user.dto.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public ResponseMessage<List<User>> getAllUser(){
        List<User> userList = userRepository.findAll();
        return new ResponseMessage<>(true,"Danh sách user ",userList);
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

        userRepository.save(user);
        return  new ResponseMessage<>(true,"Update User thành công ", user);
    }

}
