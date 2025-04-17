package com.example.coffe_shop.user.controller;


import com.example.coffe_shop.auth.model.User;
import com.example.coffe_shop.response.ResponseMessage;
import com.example.coffe_shop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<User>>> getAllUser(){
        return ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage<String>> deleteUserById(@PathVariable String id){
        return ResponseEntity.ok(userService.deleteUserbyId(id));
    }


}
