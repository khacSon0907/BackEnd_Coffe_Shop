package com.example.coffe_shop.auth.model;


import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingUser {
    private String email;

    private String password;
    private String fullname;
    private String phoneNumber;
    private String otp;
    private LocalDateTime otpRequestedTime;

}
