package com.example.coffe_shop.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mã OTP xác minh");
        message.setText("Mã OTP của bạn là: " + otp + "\n Hiệu lực trong 5 phút.");
        mailSender.send(message);
    }

    public void sendOtpForgetPassWord(String to,String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mã OTP Khôi phục mật khẩu ! ");
        message.setText("Mã OTP của bạn là : " + otp + "\n Hiệu lực trong 5 phút.");
        mailSender.send(message);
    }
}
