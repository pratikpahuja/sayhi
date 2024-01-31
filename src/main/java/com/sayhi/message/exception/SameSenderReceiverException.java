package com.sayhi.message.exception;

public class SameSenderReceiverException extends RuntimeException {

  public SameSenderReceiverException(long userId) {
    super(STR."Message sender & receiver have same user id, userId: \{userId}");
  }

}
