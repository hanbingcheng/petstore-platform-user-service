package com.example.petstore.user.service;

import com.example.petstore.common.exception.UnauthorizedException;
import com.example.petstore.common.logging.StartEndLog;
import com.example.petstore.user.entity.UserEntity;
import com.example.petstore.user.mapper.UserMapper;
import com.example.petstore.user.message.UserMessageCode;
import com.example.petstore.user.model.LoginRequest;
import com.example.petstore.user.model.LoginResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthLoginService {

  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @StartEndLog
  public LoginResponse execute(LoginRequest request) {
    UserEntity user =
        userMapper
            .findByEmail(request.getEmail())
            .orElseThrow(
                () ->
                    new UnauthorizedException(
                        UserMessageCode.LOGIN_FAILED.getCode(), "Invalid email or password"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      throw new UnauthorizedException(
          UserMessageCode.LOGIN_FAILED.getCode(), "Invalid email or password");
    }

    String token = UUID.randomUUID().toString();

    return new LoginResponse().token(token).userId(user.getId());
  }
}
