package com.sayhi.messaging.domain;

import java.time.Instant;

public class Message {
  private MessageBody body;
  private long senderUserId;
  private long receiverUserId;
  private Instant sentTimestamp;
}
