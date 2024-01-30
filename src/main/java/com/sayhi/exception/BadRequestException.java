package com.sayhi.exception;

import com.sayhi.user.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestException extends ResponseStatusException {
  public BadRequestException(RuntimeException e) {
    super(getHttpStatus(e), e.getMessage(), e);
  }

  public BadRequestException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }

  private static HttpStatus getHttpStatus(RuntimeException e) {
    if (e instanceof UserAlreadyExistsException)
      return HttpStatus.CONFLICT;

    return HttpStatus.BAD_REQUEST;
  }
}
