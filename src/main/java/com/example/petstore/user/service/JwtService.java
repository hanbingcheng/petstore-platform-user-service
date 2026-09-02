package com.example.petstore.user.service;

import com.example.petstore.user.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

/** JWT の発行を行うサービス。 */
@Service
public class JwtService {

  private final SecretKey key;
  private final Duration expiration;

  public JwtService(JwtProperties properties) {
    this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    this.expiration = properties.getExpiration();
  }

  /** ユーザーIDとメールアドレスをクレームに持つ JWT を発行する。 */
  public String generateToken(Long userId, String email) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(String.valueOf(userId))
        .claim("email", email)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(expiration)))
        .signWith(key)
        .compact();
  }
}
