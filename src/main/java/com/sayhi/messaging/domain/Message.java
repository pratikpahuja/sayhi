package com.sayhi.messaging.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_id_generator")
  @SequenceGenerator(name = "message_id_generator", sequenceName = "message_id_seq", allocationSize = 1)
  private Long id;
  private String body;
  private long senderUserId;
  private long receiverUserId;
  private Instant sentAt;
}
