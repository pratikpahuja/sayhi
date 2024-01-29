package com.sayhi.user.exception;

public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String nickName) {
    super(STR."User already exists with nick name: \{nickName}");
  }

}
