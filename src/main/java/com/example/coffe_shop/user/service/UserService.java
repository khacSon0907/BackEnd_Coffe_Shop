package com.example.coffe_shop.user.service;


import com.example.coffe_shop.auth.model.User;
import com.example.coffe_shop.auth.repository.UserRepository;
import com.example.coffe_shop.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
}
