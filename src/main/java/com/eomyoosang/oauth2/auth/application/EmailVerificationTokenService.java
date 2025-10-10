package com.eomyoosang.oauth2.auth.application;

import com.eomyoosang.oauth2.auth.application.payload.EmailVerificationPayload;
import com.eomyoosang.oauth2.config.auth.EmailVerificationProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationTokenService {

    private static final RedisScript<String> GET_AND_DELETE_SCRIPT = new DefaultRedisScript<>(
            "local value = redis.call('GET', KEYS[1]); if value then redis.call('DEL', KEYS[1]); end; return value;",
            String.class
    );

    private final StringRedisTemplate stringRedisTemplate;
    private final EmailVerificationProperties properties;
    private final ObjectMapper objectMapper;

    public EmailVerificationTokenService(StringRedisTemplate stringRedisTemplate,
                                         EmailVerificationProperties properties,
                                         ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public String createToken(UUID userId, String email) {
        EmailVerificationPayload payload = new EmailVerificationPayload(userId, email);
        String token = generateToken();
        String key = key(token);

        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        String serialized = serialize(payload);
        Duration ttl = Duration.ofSeconds(properties.getTtlSeconds());
        valueOperations.set(key, serialized, ttl);
        return token;
    }

    public Optional<EmailVerificationPayload> consumeToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        String key = key(token);
        String serialized = stringRedisTemplate.execute(GET_AND_DELETE_SCRIPT, List.of(key), new Object[0]);
        if (serialized == null) {
            return Optional.empty();
        }
        return Optional.of(deserialize(serialized));
    }

    private String key(String token) {
        return properties.getKeyPrefix() + token;
    }

    private String generateToken() {
        byte[] randomBytes = new byte[Math.max(16, properties.getTokenByteLength())];
        ThreadLocalRandom.current().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String serialize(EmailVerificationPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize email verification payload", ex);
        }
    }

    private EmailVerificationPayload deserialize(String serialized) {
        try {
            return objectMapper.readValue(serialized, EmailVerificationPayload.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to deserialize email verification payload", ex);
        }
    }
}
