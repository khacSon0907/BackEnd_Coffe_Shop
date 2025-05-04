package com.example.coffe_shop.user.controller.admin;



import com.example.coffe_shop.auth.model.User;
import com.example.coffe_shop.auth.model.UserPrincipal;
import com.example.coffe_shop.response.ResponseMessage;
import com.example.coffe_shop.user.dto.UpdateUserActiveRequest;
import com.example.coffe_shop.user.dto.UpdateUserRequest;
import com.example.coffe_shop.user.dto.UpdateUserRoleRequest;
import com.example.coffe_shop.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor

public class AdminUserController {

    private final UserService userService ;


    @PutMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMessage<String>> updateUserActive(
            @PathVariable String id,
            @RequestBody UpdateUserActiveRequest request
    ) {
        return ResponseEntity.ok(userService.updateUserActive(id, request.isActive()));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMessage<String>> updateUserRole(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRoleRequest request,
            @AuthenticationPrincipal UserPrincipal admin) {
        return ResponseEntity.ok(userService.updateUserRole(id, admin.getEmail(), request));
    }



    @GetMapping("/find-userId")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public  ResponseEntity<ResponseMessage<Optional<User>>> findUserById(@RequestParam("id") String id){
        return ResponseEntity.ok(userService.getUserById(id));
    }



    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/read-users")
    public ResponseEntity<ResponseMessage<List<User>>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUser());
    }
}
