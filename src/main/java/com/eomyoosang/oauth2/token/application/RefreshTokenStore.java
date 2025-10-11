package com.eomyoosang.oauth2.token.application;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenStore {

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    public RefreshTokenStore(StringRedisTemplate redisTemplate, JwtTokenProvider jwtTokenProvider) {
        this.redisTemplate = redisTemplate;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void store(UUID userId, String deviceId, String refreshToken) {
        String key = refreshKey(userId, deviceId);
        Duration ttl = Duration.ofSeconds(jwtTokenProvider.refreshTokenTtlSeconds());
        redisTemplate.opsForValue().set(key, refreshToken, ttl);

        String deviceRegistryKey = deviceRegistryKey(userId);
        redisTemplate.opsForSet().add(deviceRegistryKey, deviceId);
        redisTemplate.expire(deviceRegistryKey, ttl);
    }

    public Optional<String> find(UUID userId, String deviceId) {
        String stored = redisTemplate.opsForValue().get(refreshKey(userId, deviceId));
        if (stored == null) {
            return Optional.empty();
        }
        return Optional.of(stored);
    }

    public void delete(UUID userId, String deviceId) {
        redisTemplate.delete(refreshKey(userId, deviceId));
    }

    public String generateDeviceId() {
        return UUID.randomUUID().toString();
    }

    private String refreshKey(UUID userId, String deviceId) {
        return jwtTokenProvider.refreshKeyPrefix() + userId + ":" + deviceId;
    }

    private String deviceRegistryKey(UUID userId) {
        return jwtTokenProvider.deviceRegistryPrefix() + userId;
    }
}
