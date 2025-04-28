package com.example.coffe_shop.user.controller.admin;



import com.example.coffe_shop.auth.model.User;
import com.example.coffe_shop.response.ResponseMessage;
import com.example.coffe_shop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor

public class AdminUserController {

    private final UserService userService ;
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test")
    public String sayHelloTest(){
        return " I am Arthur - admin";
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
