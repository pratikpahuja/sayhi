package com.sayhi.messaging.service;

import com.sayhi.messaging.domain.Message;
import com.sayhi.messaging.domain.MessageSentStatus;
import com.sayhi.messaging.repository.MessageRepository;
import com.sayhi.user.domain.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingSevice {

  private final MessageRepository repository;

  public MessageSentStatus sendMessage(Message message) {
    try {
      repository.save(message);
      return MessageSentStatus.SUCCESS;
    } catch (RuntimeException e) {
      log.error(STR."Event: message_sending. Exception raised while sending message: \{e.getMessage()}", e);
      return MessageSentStatus.FAILED;
    }
  }

  public List<Message> getMessages(Optional<AppUser> sender, Optional<AppUser> receiver) {
    if (sender.isEmpty() && receiver.isEmpty())
      throw new IllegalArgumentException("Event: get_messages. Error: Both sender and receiver are empty.");

    if (sender.isPresent() && receiver.isPresent())
      return repository.findByReceiverUserIdAndSenderUserIdOrderBySentAtDesc(receiver.get().getId(), sender.get().getId());

    return sender
      .map(AppUser::getId)
      .map(repository::findBySenderUserIdOrderBySentAtDesc)
      .orElseGet(() -> repository.findByReceiverUserIdOrderBySentAtDesc(receiver.get().getId()));
  }
}
