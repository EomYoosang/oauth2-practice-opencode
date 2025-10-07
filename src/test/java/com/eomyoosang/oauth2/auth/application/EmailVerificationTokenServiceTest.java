package com.eomyoosang.oauth2.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eomyoosang.oauth2.auth.application.payload.EmailVerificationPayload;
import com.eomyoosang.oauth2.config.auth.EmailVerificationProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailVerificationTokenServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private EmailVerificationTokenService tokenService;

    @BeforeEach
    void setUp() {
        EmailVerificationProperties properties = new EmailVerificationProperties();
        properties.setKeyPrefix("auth:ev:");
        properties.setTtlSeconds(600);
        properties.setTokenByteLength(24);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

        tokenService = new EmailVerificationTokenService(stringRedisTemplate, properties, new ObjectMapper());
    }

    @DisplayName("토큰 생성 시 Redis에 직렬화된 페이로드와 TTL이 저장된다")
    @Test
    void createTokenStoresPayload() {
        UUID userId = UUID.randomUUID();

        String token = tokenService.createToken(userId, "user@example.com");

        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(keyCaptor.capture(), valueCaptor.capture(), eq(Duration.ofSeconds(600)));

        assertThat(keyCaptor.getValue()).startsWith("auth:ev:");
        assertThat(token).isNotBlank();
        assertThat(valueCaptor.getValue()).contains("user@example.com");
    }

    @DisplayName("토큰 소비 시 Redis Lua 스크립트 결과가 매핑된다")
    @Test
    void consumeTokenReturnsPayload() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = "sample-token";
        EmailVerificationPayload payload = new EmailVerificationPayload(userId, "user@example.com");
        String serialized = new ObjectMapper().writeValueAsString(payload);

        doReturn(serialized).when(stringRedisTemplate)
                .execute(any(RedisScript.class), eq(List.of("auth:ev:" + token)), any(Object[].class));

        Optional<EmailVerificationPayload> result = tokenService.consumeToken(token);
        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo("user@example.com");
    }

    @DisplayName("토큰 직렬화에 실패하면 예외를 던진다")
    @Test
    void serializationFailureThrows() {
        EmailVerificationProperties properties = new EmailVerificationProperties();
        tokenService = new EmailVerificationTokenService(stringRedisTemplate, properties, new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JsonProcessingException {
                throw new JsonProcessingException("boom") {
                };
            }
        });

        assertThatThrownBy(() -> tokenService.createToken(UUID.randomUUID(), "user@example.com"))
                .isInstanceOf(IllegalStateException.class);
    }
}
