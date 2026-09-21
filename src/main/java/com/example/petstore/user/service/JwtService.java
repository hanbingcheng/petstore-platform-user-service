package com.example.petstore.user.service;

import com.example.petstore.common.exception.UnauthorizedException;
import com.example.petstore.user.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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

  /** トークンを検証し、有効期限切れを許容してユーザーIDとメールアドレスを取り出す。 */
  public UserClaims parseForRefresh(String token) {
    try {
      Claims claims;
      try {
        claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
      } catch (ExpiredJwtException e) {
        // 有効期限切れでも署名が正しければ、クレームを取り出して再発行を許可する
        claims = e.getClaims();
      }
      return new UserClaims(Long.valueOf(claims.getSubject()), claims.get("email", String.class));
    } catch (JwtException | IllegalArgumentException e) {
      throw new UnauthorizedException("Invalid or expired token");
    }
  }

  /** 検証済みトークンから取り出したクレーム。 */
  public record UserClaims(Long userId, String email) {}
}
