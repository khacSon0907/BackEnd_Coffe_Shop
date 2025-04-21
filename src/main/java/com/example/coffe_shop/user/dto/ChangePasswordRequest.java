package com.example.coffe_shop.user.dto;

import lombok.Data;

@Data

public class ChangePasswordRequest {
    private String password;
    private String newPassword;

}
