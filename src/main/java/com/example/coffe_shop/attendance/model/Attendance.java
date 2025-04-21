package com.example.coffe_shop.attendance.model;


import lombok.*;
        import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "attendances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    private String id;
    private String userId;
    private LocalDateTime checkInTime;      // giovao
    private LocalDateTime checkOutTime;     // gio ra
    private String note; // Ghi chú (nếu có)

}
