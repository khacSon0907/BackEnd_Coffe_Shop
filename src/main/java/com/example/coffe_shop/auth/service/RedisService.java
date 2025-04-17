package com.example.coffe_shop.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public void saveOtp(String token, String jsonData, long timeoutMinutes) {
        redisTemplate.opsForValue().set("otp_token:" + token, jsonData, timeoutMinutes, TimeUnit.MINUTES);
    }

    public String getOtp(String token) {
        return redisTemplate.opsForValue().get("otp_token:" + token);
    }

    public void deleteOtp(String token) {
        redisTemplate.delete("otp_token:" + token);
    }


    public void saveRefreshToken(String email, String refreshToken, long minutes) {
        redisTemplate.opsForValue().set("refresh:" + email, refreshToken, Duration.ofMinutes(minutes));
    }

    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get("refresh:" + email);
    }

    public void deleteRefreshToken(String email) {
        redisTemplate.delete("refresh:" + email);
    }
}
