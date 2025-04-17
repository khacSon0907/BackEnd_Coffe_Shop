
package com.example.coffe_shop.auth.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String token;
    private String otp;
}
