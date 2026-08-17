package com.example.petstore.user.controller;

import com.example.petstore.user.api.LoginApiDelegate;
import com.example.petstore.user.model.LoginRequest;
import com.example.petstore.user.model.LoginResponse;
import com.example.petstore.user.service.AuthLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthApiDelegateImpl implements LoginApiDelegate {

  private final AuthLoginService authLoginService;

  @Override
  public ResponseEntity<LoginResponse> login(LoginRequest request) {
    return ResponseEntity.ok(authLoginService.execute(request));
  }
}
