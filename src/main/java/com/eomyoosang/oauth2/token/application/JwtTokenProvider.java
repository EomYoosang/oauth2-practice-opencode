package com.eomyoosang.oauth2.token.application;

import com.eomyoosang.oauth2.config.token.JwtProperties;
import com.eomyoosang.oauth2.token.exception.InvalidRefreshTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String CLAIM_DEVICE_ID = "deviceId";

    private final JwtProperties properties;
    private final Clock clock;

    private Key signingKey;
    private String issuer;

    public JwtTokenProvider(JwtProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    @PostConstruct
    void initialize() {
        String secret = Objects.requireNonNull(properties.getSecret(), "jwt.secret must be configured");
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 256 bits (32 bytes)");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        String configuredIssuer = properties.getIssuer();
        if (configuredIssuer == null || configuredIssuer.isBlank()) {
            this.issuer = "oauth2-practice";
        } else {
            this.issuer = configuredIssuer;
        }
    }

    public IssuedToken createAccessToken(UUID userId, String deviceId) {
        long ttl = properties.getAccessToken().getTtlSeconds();
        return createToken(userId, deviceId, ttl, TokenType.ACCESS);
    }

    public IssuedToken createRefreshToken(UUID userId, String deviceId) {
        long ttl = properties.getRefreshToken().getTtlSeconds();
        return createToken(userId, deviceId, ttl, TokenType.REFRESH);
    }

    public ParsedJwt parse(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .requireIssuer(issuer)
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            Claims claims = jws.getBody();
            TokenType tokenType = TokenType.valueOf(claims.get(CLAIM_TOKEN_TYPE, String.class));
            UUID userId = UUID.fromString(claims.getSubject());
            String deviceId = claims.get(CLAIM_DEVICE_ID, String.class);
            Instant expiresAt = claims.getExpiration().toInstant();
            return new ParsedJwt(token, tokenType, userId, deviceId, expiresAt);
        } catch (IllegalArgumentException | JwtException ex) {
            throw new InvalidRefreshTokenException(ex);
        }
    }

    private IssuedToken createToken(UUID userId, String deviceId, long ttlSeconds, TokenType tokenType) {
        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plusSeconds(ttlSeconds);

        String token = Jwts.builder()
                .setIssuer(issuer)
                .setSubject(userId.toString())
                .setIssuedAt(java.util.Date.from(issuedAt))
                .setExpiration(java.util.Date.from(expiresAt))
                .claim(CLAIM_DEVICE_ID, deviceId)
                .claim(CLAIM_TOKEN_TYPE, tokenType.name())
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        return new IssuedToken(token, expiresAt, tokenType);
    }

    public long accessTokenTtlSeconds() {
        return properties.getAccessToken().getTtlSeconds();
    }

    public long refreshTokenTtlSeconds() {
        return properties.getRefreshToken().getTtlSeconds();
    }

    public String refreshKeyPrefix() {
        return properties.getRefreshToken().getKeyPrefix();
    }

    public String deviceRegistryPrefix() {
        return properties.getRefreshToken().getDeviceRegistryPrefix();
    }

    public record ParsedJwt(String value, TokenType tokenType, UUID userId, String deviceId, Instant expiresAt) {
    }
}
