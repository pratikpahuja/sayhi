package com.sayhi.message.service;

import com.sayhi.message.exception.SameSenderReceiverException;
import com.sayhi.message.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jms.core.JmsTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.sayhi.fixtures.MessageFixtures.sampleMessage;
import static com.sayhi.fixtures.UserFixtures.sampleUser;
import static com.sayhi.message.domain.MessageSentStatus.FAILED;
import static com.sayhi.message.domain.MessageSentStatus.SUCCESS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static java.util.Optional.*;

class MessagingServiceTest {

  MessagingSevice service;
  private MessageRepository mockRepository;
  private JmsTemplate mockJmsTemplate;

  @BeforeEach
  void setup() {
    mockRepository = mock(MessageRepository.class);
    mockJmsTemplate = mock(JmsTemplate.class);
    service = new MessagingSevice(mockJmsTemplate, mockRepository);
  }

  @Test
  void sendMessageWhenSameSenderAndReceiver() {
    var sender = sampleUser(213L, "nick-213");
    var receiver = sampleUser(213L, "nick-213");
    var message = sampleMessage(sender, receiver, Instant.now()).build();

    assertThrows(SameSenderReceiverException.class, () -> service.sendMessage(message));
  }

  @Test
  void sendMessage() {
    var sender = sampleUser(213L, "nick-213");
    var receiver = sampleUser(513L, "nick-513");
    var message = sampleMessage(sender, receiver, Instant.now()).build();

    var status = service.sendMessage(message);

    verify(mockJmsTemplate).convertAndSend("chatbox", message);
    assertThat(status, is(SUCCESS));
  }

  @Test
  void sendMessageFailed() {
    var sender = sampleUser(213L, "nick-213");
    var receiver = sampleUser(513L, "nick-513");
    var message = sampleMessage(sender, receiver, Instant.now()).build();
    doThrow(RuntimeException.class).when(mockJmsTemplate).convertAndSend("chatbox", message);

    var status = service.sendMessage(message);

    verify(mockJmsTemplate).convertAndSend("chatbox", message);
    assertThat(status, is(FAILED));
  }

  @Test
  void getMessagesWhenNoSenderAndReceiverSupplied() {
    assertThrows(IllegalArgumentException.class, () -> service.getMessages(Optional.empty(), Optional.empty()));
  }

  @Test
  void getMessagesWhenSenderAndReceiverSupplied() {
    var sender = sampleUser(213L, "nick-213");
    var receiver = sampleUser(513L, "nick-513");

    var messages = service.getMessages(of(sender), of(receiver));

    verify(mockRepository).findByReceiverUserIdAndSenderUserIdOrderBySentAtDesc(513L, 213L);
  }

  @Test
  void getMessagesWhenSenderSupplied() {
    var sender = sampleUser(213L, "nick-213");
    var receiver = sampleUser(513L, "nick-513");
    var message = sampleMessage(sender, receiver, Instant.now()).build();
    when(mockRepository.findBySenderUserIdOrderBySentAtDesc(213L)).thenReturn(List.of(message));

    var messages = service.getMessages(of(sender), empty());

    verify(mockRepository).findBySenderUserIdOrderBySentAtDesc(213L);
  }

  @Test
  void getMessagesWhenReceiverSupplied() {
    var receiver = sampleUser(513L, "nick-513");

    var messages = service.getMessages(empty(), of(receiver));

    verify(mockRepository).findByReceiverUserIdOrderBySentAtDesc(513L);
  }
}