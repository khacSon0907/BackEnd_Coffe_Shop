package com.example.coffe_shop.user.controller.user;

import com.example.coffe_shop.auth.dto.JwtResponse;
import com.example.coffe_shop.auth.model.UserPrincipal;
import com.example.coffe_shop.auth.model.User;
import com.example.coffe_shop.response.ResponseMessage;
import com.example.coffe_shop.user.dto.ChangePasswordRequest;
import com.example.coffe_shop.user.dto.UpdateUserRequest;
import com.example.coffe_shop.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ResponseMessage<String>> deleteUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.deleteUserbyId(id));
    }



    @PutMapping("/me/change-password")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ResponseMessage<String>> changePassword(
             @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userService.changePassword(userPrincipal.getEmail(), request));
    }

    @PutMapping("/update-user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ResponseMessage<User>> updateUser(
            @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userService.updateUser(userPrincipal.getEmail(), request));
    }
}
