package com.example.petstore.user.service;

import com.example.petstore.common.logging.StartEndLog;
import com.example.petstore.user.model.LoginResponse;
import com.example.petstore.user.model.RefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 既存トークンを検証し、有効期限を延長した新しいトークンを再発行するサービス。 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

  private final JwtService jwtService;

  @StartEndLog
  public LoginResponse execute(RefreshTokenRequest request) {
    JwtService.UserClaims claims = jwtService.parseForRefresh(request.getToken());
    String token = jwtService.generateToken(claims.userId(), claims.email());
    return new LoginResponse().token(token).userId(claims.userId());
  }
}
