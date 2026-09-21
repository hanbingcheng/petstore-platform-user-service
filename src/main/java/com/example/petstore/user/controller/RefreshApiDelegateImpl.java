package com.example.petstore.user.controller;

import com.example.petstore.user.api.RefreshApiDelegate;
import com.example.petstore.user.model.LoginResponse;
import com.example.petstore.user.model.RefreshTokenRequest;
import com.example.petstore.user.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshApiDelegateImpl implements RefreshApiDelegate {

  private final RefreshTokenService refreshTokenService;

  @Override
  public ResponseEntity<LoginResponse> refreshToken(RefreshTokenRequest request) {
    return ResponseEntity.ok(refreshTokenService.execute(request));
  }
}
