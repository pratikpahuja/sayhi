package com.sayhi.user.api;

import com.sayhi.exception.BadRequestException;
import com.sayhi.exception.UnknownException;
import com.sayhi.user.api.interfaces.CreateUserRequest;
import com.sayhi.user.api.interfaces.CreateUserResponse;
import com.sayhi.user.domain.AppUser;
import com.sayhi.user.exception.UserAlreadyExistsException;
import com.sayhi.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import static java.util.stream.Collectors.joining;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public CreateUserResponse create(@RequestBody @Valid CreateUserRequest request, BindingResult bindingResult) {
    validationCheck(bindingResult);


    try {
      var createdUser = userService.createUser(mapToUser(request));
      return mapToResponse(createdUser);
    } catch (UserAlreadyExistsException e) {
      throw new BadRequestException(e);
    } catch (Exception e) {
      throw new UnknownException(e);
    }
  }

  private CreateUserResponse mapToResponse(AppUser createdUser) {
    return CreateUserResponse.builder()
      .user(createdUser)
      .build();
  }

  private AppUser mapToUser(CreateUserRequest request) {
    return AppUser.builder()
      .nickName(request.getNickName())
      .build();
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
