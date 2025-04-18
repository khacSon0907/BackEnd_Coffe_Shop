package com.example.coffe_shop.auth.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {


    @GetMapping("/admin/hello")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminHello() {
        return "👑 Xin chào ADMIN!";
    }

    @GetMapping("/users/hello")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String userHello() {
        return "🙋 Xin chào USER hoặc ADMIN!";
    }
}
