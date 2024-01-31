package com.sayhi.message.service;

import com.sayhi.message.domain.Message;
import com.sayhi.message.domain.MessageSentStatus;
import com.sayhi.message.exception.SameSenderReceiverException;
import com.sayhi.message.repository.MessageRepository;
import com.sayhi.user.domain.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.sayhi.message.service.MessageQueueConstants.MESSAGE_QUEUE_NAME;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingSevice {

  private final JmsTemplate jmsTemplate;
  private final MessageRepository repository;

  public MessageSentStatus sendMessage(Message message) {
    performSameSenderReceiverCheck(message.getSenderUserId(), message.getReceiverUserId());

    try {
      jmsTemplate.convertAndSend(MESSAGE_QUEUE_NAME, message);
      return MessageSentStatus.SUCCESS;
    } catch (RuntimeException e) {
      log.error(STR."Event: message_sending. Exception raised while sending message: \{e.getMessage()}", e);
      return MessageSentStatus.FAILED;
    }
  }

  public List<Message> getMessages(Optional<AppUser> sender, Optional<AppUser> receiver) {
    if (sender.isEmpty() && receiver.isEmpty())
      throw new IllegalArgumentException("Event: get_messages. Error: Both sender and receiver are empty.");

    if (sender.isPresent() && receiver.isPresent()) {
      performSameSenderReceiverCheck(sender.get().getId(), receiver.get().getId());

      return repository.findByReceiverUserIdAndSenderUserIdOrderBySentAtDesc(receiver.get().getId(), sender.get().getId());
    }

    return sender
      .map(AppUser::getId)
      .map(repository::findBySenderUserIdOrderBySentAtDesc)
      .orElseGet(() -> repository.findByReceiverUserIdOrderBySentAtDesc(receiver.get().getId()));
  }

  private void performSameSenderReceiverCheck(long senderUserId, long receiverUserId) {
    if (senderUserId == receiverUserId)
      throw new SameSenderReceiverException(senderUserId);
  }
}
