package com.sayhi.user.api.interfaces;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
  @NotNull(message = "nickName cannot be null.")
  @Size(min = 1, max = 250, message = "nickName cannot be less than 1 or more than 250")
  private String nickName;
}
