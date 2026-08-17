package com.example.petstore.user.service;

import com.example.petstore.common.exception.ResourceNotFoundException;
import com.example.petstore.common.logging.StartEndLog;
import com.example.petstore.user.entity.UserEntity;
import com.example.petstore.user.mapper.UserMapper;
import com.example.petstore.user.message.UserMessageCode;
import com.example.petstore.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserGetService {

  private final UserMapper userMapper;

  @StartEndLog
  public User execute(Long id) {
    UserEntity user =
        userMapper
            .findById(id)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        UserMessageCode.USER_NOT_FOUND.getCode(), "User not found with id: " + id));

    return new User()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .createdAt(user.getCreatedAt());
  }
}
