package com.example.coffe_shop.attendance.model;


import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "attendances")
public class Attendance {
    @Id
    private String id;

    private String userId;                     // ID nhân viên
    private LocalDateTime checkInTime;         // Giờ vào
    private LocalDateTime checkOutTime;        // Giờ ra
    private String note;                       // Ghi chú (nếu có)
    private String ip;                         // IP thiết bị gửi lên
    private LocalDate date;                    // Ngày chấm công (YYYY-MM-DD)
}
