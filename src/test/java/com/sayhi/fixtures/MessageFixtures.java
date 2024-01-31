package com.sayhi.fixtures;

import com.sayhi.message.domain.Message;
import com.sayhi.user.domain.AppUser;

import java.time.Instant;

public class MessageFixtures {

  public static Message.MessageBuilder sampleMessage(AppUser sender, AppUser receiver, Instant sentAt) {
    return Message.builder()
      .body("sample-body")
      .senderUserId(sender.getId())
      .receiverUserId(receiver.getId())
      .sentAt(sentAt);
  }

}
