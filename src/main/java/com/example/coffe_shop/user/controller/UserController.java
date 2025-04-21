package com.example.coffe_shop.user.controller;


import com.example.coffe_shop.auth.model.User;
import com.example.coffe_shop.response.ResponseMessage;
import com.example.coffe_shop.user.dto.ChangePasswordRequest;
import com.example.coffe_shop.user.dto.UpdateUserRequest;
import com.example.coffe_shop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ResponseMessage<List<User>>> getAllUser(){
        return ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage<String>> deleteUserById(@PathVariable String id){
        return ResponseEntity.ok(userService.deleteUserbyId(id));
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ResponseMessage<String>> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(userService.changePassword(email, request));
    }


    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ResponseMessage<User>> updateUser(@RequestBody UpdateUserRequest request,
                                                            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(userService.updateUser(email, request));
    }
}
