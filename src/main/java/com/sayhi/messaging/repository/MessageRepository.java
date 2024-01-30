package com.sayhi.messaging.repository;

import com.sayhi.messaging.domain.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {
  List<Message> findByReceiverUserIdOrderBySentAtDesc(long receiverId);
  List<Message> findBySenderUserIdOrderBySentAtDesc(long senderId);

  List<Message> findByReceiverUserIdAndSenderUserIdOrderBySentAtDesc(long receiverId, long senderId);
}
