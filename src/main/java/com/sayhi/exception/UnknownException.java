package com.sayhi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnknownException extends ResponseStatusException {
  public UnknownException(Exception e) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error.", e);
  }

  public UnknownException(String message) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, message);
  }
}
