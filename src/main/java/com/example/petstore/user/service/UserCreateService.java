package com.example.petstore.user.service;

import com.example.petstore.common.exception.DuplicateResourceException;
import com.example.petstore.common.logging.StartEndLog;
import com.example.petstore.user.entity.UserEntity;
import com.example.petstore.user.mapper.UserMapper;
import com.example.petstore.user.message.UserMessageCode;
import com.example.petstore.user.model.CreateUserRequest;
import com.example.petstore.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCreateService {

  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @StartEndLog
  public User execute(CreateUserRequest request) {
    if (userMapper.existsByEmail(request.getEmail())) {
      throw new DuplicateResourceException(
          UserMessageCode.USER_DUPLICATE.getCode(),
          "User already exists with email: " + request.getEmail());
    }

    UserEntity userEntity =
        UserEntity.builder()
            .name(request.getName())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .build();

    userMapper.insert(userEntity);

    return new User()
        .id(userEntity.getId())
        .name(userEntity.getName())
        .email(userEntity.getEmail())
        .createdAt(userEntity.getCreatedAt());
  }
}
