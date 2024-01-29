package com.sayhi.user.service;

import com.sayhi.user.domain.AppUser;
import com.sayhi.user.exception.UserAlreadyExistsException;
import com.sayhi.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final AppUserRepository repository;

  public AppUser createUser(AppUser user) {
    checkNickNameUniqueness(user);

    return repository.save(user);
  }

  private void checkNickNameUniqueness(AppUser user) {
    var userByNickName = repository.findByNickName(user.getNickName());
    if (userByNickName.isPresent()) {
      log.error(STR."Event: User creation. Error: User with same nick name exists \{user.getNickName()}, id: \{userByNickName.get().getId()}");
      throw new UserAlreadyExistsException(user.getNickName());
    }
  }

}
