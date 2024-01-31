package com.sayhi.message.api;

import com.sayhi.config.TestClockConfig;
import com.sayhi.message.domain.Message;
import com.sayhi.message.exception.SameSenderReceiverException;
import com.sayhi.message.service.MessagingSevice;
import com.sayhi.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;

import static com.sayhi.fixtures.UserFixtures.sampleUser;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@Import(TestClockConfig.class)
class MessageControllerMVCTest {

  @Autowired MockMvc mvc;
  @MockBean MessagingSevice messagingSevice;
  @MockBean UserService userService;

  @Test
  void submitMessageWhenSameSenderReceiver() throws Exception {
    var sender = sampleUser(123L, "user-sender");
    var receiver = sampleUser(123L, "user-sender");
    when(userService.findUserById(sender.getId())).thenReturn(of(sender));
    when(userService.findUserById(receiver.getId())).thenReturn(of(receiver));
    when(messagingSevice.sendMessage(any())).thenThrow(SameSenderReceiverException.class);

    performSubmitMessage(sender.getId(), submitMessageRequestBody("msg", receiver.getId()))
      .andExpect(status().isBadRequest());
  }

  @Test
  void submitMessage() throws Exception {
    var sender = sampleUser(123L, "user-sender");
    var receiver = sampleUser(513L, "user-receiver");
    when(userService.findUserById(sender.getId())).thenReturn(of(sender));
    when(userService.findUserById(receiver.getId())).thenReturn(of(receiver));

    performSubmitMessage(sender.getId(), submitMessageRequestBody("msg", receiver.getId()))
      .andExpect(status().isOk());

    verify(userService).findUserById(sender.getId());
    verify(userService).findUserById(receiver.getId());
    verify(messagingSevice).sendMessage(expectedMessage());
  }

  private Message expectedMessage() {
    return Message.builder()
      .senderUserId(123L)
      .receiverUserId(513L)
      .body("msg")
      .sentAt(Instant.parse("2024-01-01T13:10:00Z"))
      .build();
  }

  String submitMessageRequestBody(String messageBody, long receiverId) {
    return STR."""
      {
        "receiverUserId": \{receiverId},
        "messageBody": "\{messageBody}"
      }
      """;
  }

  ResultActions performSubmitMessage(long userId, String requestBody) throws Exception {
    return mvc.perform(post("/api/v1/messages")
      .contentType(MediaType.APPLICATION_JSON)
        .header("x-user-id", userId)
      .content(requestBody));


  }
}