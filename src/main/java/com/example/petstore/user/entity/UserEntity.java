package com.example.petstore.user.entity;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
  private Long id;
  private String name;
  private String email;
  private String passwordHash;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
