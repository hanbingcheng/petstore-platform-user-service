package com.example.petstore.user.mapper;

import com.example.petstore.user.entity.UserEntity;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
  Optional<UserEntity> findById(Long id);

  Optional<UserEntity> findByEmail(String email);

  boolean existsByEmail(String email);

  void insert(UserEntity user);

  void update(UserEntity user);

  void deleteById(Long id);
}
