package com.sayhi.messaging.api;

import com.sayhi.exception.BadRequestException;
import com.sayhi.exception.ResourceNotFoundException;
import com.sayhi.exception.UnknownException;
import com.sayhi.messaging.api.interfaces.SubmitMessageRequest;
import com.sayhi.messaging.domain.Message;
import com.sayhi.messaging.domain.MessageSentStatus;
import com.sayhi.messaging.service.MessagingSevice;
import com.sayhi.user.domain.AppUser;
import com.sayhi.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessagingSevice messagingSevice;
  private final UserService userService;
  private final Clock clock;

  @PostMapping
  void submit(@RequestHeader(name = "x-user-id") long userId, @RequestBody @Valid SubmitMessageRequest request, BindingResult bindingResult) {
    validationCheck(bindingResult);
    getUserOrFail(userId);
    getUserOrFail(request.getReceiverUserId());

    try {
      var status = messagingSevice.sendMessage(toMessage(userId, request));

      if (status == MessageSentStatus.FAILED)
        throw new UnknownException("Message sending failed");
    } catch (Exception e) {
      throw new UnknownException(e);
    }
  }

  @GetMapping
  List<Message> getMessages(@RequestHeader(name = "x-user-id") long userId,
                   @RequestParam(name = "transfer_type") MessageTransferType transferType,
                   @RequestParam(name = "fellow_user_id", required = false) Optional<Long> fellowUserId) {

    var mainUser = getUserOrFail(userId);

    var messages = List.<Message>of();
    if (fellowUserId.isPresent()) {
      var fellowUser = getUserOrFail(fellowUserId.get());

      if (transferType == MessageTransferType.SENT) {
        //Sent from main to fellow user
        messages = messagingSevice.getMessages(of(mainUser), of(fellowUser));
      } else {
        //Received by main from fellow user
        messages = messagingSevice.getMessages(of(fellowUser), of(mainUser));
      }
    } else {
      //All sent messages
      if (transferType == MessageTransferType.SENT) {
        messages = messagingSevice.getMessages(of(mainUser), empty());
      } else {
        //All received messages
        messages = messagingSevice.getMessages(empty(), of(mainUser));
      }
    }

    return messages;
  }

  private Message toMessage(long senderUserId, SubmitMessageRequest request) {
    return Message.builder()
      .senderUserId(senderUserId)
      .receiverUserId(request.getReceiverUserId())
      .body(request.getMessageBody())
      .sentAt(Instant.now(clock))
      .build();
  }

  private AppUser getUserOrFail(long userId) {
    return userService.findUserById(userId)
      .orElseThrow(() -> new ResourceNotFoundException(STR."No user exists for id: \{userId}"));
  }

  private static void validationCheck(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      var validationErrorMessage = bindingResult.getAllErrors().stream()
        .map(ObjectError::getDefaultMessage)
        .collect(joining(","));

      throw new BadRequestException(validationErrorMessage);
    }
  }
}
