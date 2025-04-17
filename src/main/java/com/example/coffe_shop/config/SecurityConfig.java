
package com.example.coffe_shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF cho API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**","/api/test/**","api/users/**").permitAll() // 👈 Cho phép đăng ký / đăng nhập không cần auth
                        .anyRequest().authenticated() // Các request khác phải login mới dùng được
                )
                .formLogin(form -> form.disable()) // 👈 Tắt form login mặc định
                .httpBasic(Customizer.withDefaults()) // Dùng basic auth (tùy chọn)
                .build();
    }
}
