package com.sayhi.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_generator")
  @SequenceGenerator(name = "app_user_generator", sequenceName = "app_user_id_seq", allocationSize = 1)
  private long id;
  private String nickName;
}
