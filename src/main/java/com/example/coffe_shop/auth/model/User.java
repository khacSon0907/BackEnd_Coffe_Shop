package com.example.coffe_shop.auth.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;                          //id user
    private String email;                       //email
    private String fullname ;                   // Tên uer
    private String password ;                   // mật khẩu
    private String phoneNumber;                 // số điện thoại
    private String role;
    private boolean active;                     // Trạng thái hoạt động (đã nghỉ việc?)
    private String gender;                      // Giới tính
    private LocalDate dateOfBirth;              // ngay sinh nam sinh
    private LocalDate createdAt;                // Ngày tao tai khoan
    private String avatarUrl;                    // Link ảnh đại diện
    private String address;                      // địa chỉ ;
    private String allowedIp;   
}
