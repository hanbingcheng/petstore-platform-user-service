package com.example.petstore.user.controller;

import com.example.petstore.user.api.UsersApiDelegate;
import com.example.petstore.user.model.CreateUserRequest;
import com.example.petstore.user.model.User;
import com.example.petstore.user.service.UserCreateService;
import com.example.petstore.user.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersApiDelegateImpl implements UsersApiDelegate {

  private final UserCreateService userCreateService;
  private final UserGetService userGetService;

  @Override
  public ResponseEntity<User> createUser(CreateUserRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userCreateService.execute(request));
  }

  @Override
  public ResponseEntity<User> getUserById(Long userId) {
    return ResponseEntity.ok(userGetService.execute(userId));
  }
}
