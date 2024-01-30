package com.sayhi.messaging.api.interfaces;

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
public class SubmitMessageRequest {

  @NotNull
  private long receiverUserId;
  @NotNull
  @Size(max = 1000)
  private String messageBody;

}
