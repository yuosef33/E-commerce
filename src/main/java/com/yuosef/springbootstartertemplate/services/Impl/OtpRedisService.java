package com.yuosef.springbootstartertemplate.services.Impl;

import com.yuosef.springbootstartertemplate.models.Dtos.PendingUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void savePendingUser(String email, PendingUser pendingUser) {

        redisTemplate.opsForValue()
                .set("otp:" + email, pendingUser, Duration.ofMinutes(5));
    }

    public PendingUser getPendingUser(String email) {

        return (PendingUser) redisTemplate.opsForValue()
                .get("otp:" + email);
    }

    public void deletePendingUser(String email) {

        redisTemplate.delete("otp:" + email);
    }

    public String generateOtp() {
        return String.valueOf(new SecureRandom().nextInt(900000) + 100000);
    }
}