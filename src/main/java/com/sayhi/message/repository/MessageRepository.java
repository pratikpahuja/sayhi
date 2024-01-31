package com.sayhi.message.repository;

import com.sayhi.message.domain.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {
  List<Message> findByReceiverUserIdOrderBySentAtDesc(long receiverId);
  List<Message> findBySenderUserIdOrderBySentAtDesc(long senderId);

  List<Message> findByReceiverUserIdAndSenderUserIdOrderBySentAtDesc(long receiverId, long senderId);
}
