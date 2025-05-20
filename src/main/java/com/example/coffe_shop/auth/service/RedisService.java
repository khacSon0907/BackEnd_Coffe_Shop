package com.example.coffe_shop.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
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
    public String getEmailFromToken(String token) {
        String json = redisTemplate.opsForValue().get("otp_token:" + token);
        if (json == null) {
            return null;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);
            return node.get("email").asText();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc email từ token Redis", e);
        }
    }


    public void saveRefreshToken(String email, String refreshToken, long minutes) {
        redisTemplate.opsForValue().set("refresh:" + email, refreshToken, Duration.ofMinutes(minutes));
    }

    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get("refresh:" + email);
    }

    public String getEmailFromRefreshToken(String refreshToken) {
        Set<String> keys = redisTemplate.keys("refresh:*");
        if (keys != null) {
            for (String key : keys) {
                String value = redisTemplate.opsForValue().get(key);
                if (refreshToken.equals(value)) {
                    return key.replace("refresh:", "");
                }
            }
        }
        return null;
    }


    public void deleteRefreshToken(String email) {
        redisTemplate.delete("refresh:" + email);
    }
}
