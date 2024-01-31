package com.sayhi.message.service;

import com.sayhi.message.domain.Message;
import com.sayhi.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static com.sayhi.message.service.MessageQueueConstants.MESSAGE_QUEUE_NAME;

@Component
@RequiredArgsConstructor
public class MessageQueueListener {
  private final MessageRepository repository;

  @JmsListener(destination = MESSAGE_QUEUE_NAME)
  public void saveMessage(Message message) {
    repository.save(message);
  }
}
