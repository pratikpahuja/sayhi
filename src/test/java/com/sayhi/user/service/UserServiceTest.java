package com.sayhi.user.service;

import com.sayhi.user.domain.AppUser;
import com.sayhi.user.exception.UserAlreadyExistsException;
import com.sayhi.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sayhi.fixtures.UserFixtures.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {
  UserService service;
  private AppUserRepository mockUserRepository;

  @BeforeEach
  void setup() {
    mockUserRepository = mock(AppUserRepository.class);
    service = new UserService(mockUserRepository);
  }

  @Test
  void createUser() {
    var user = sampleUser("test");
    when(mockUserRepository.save(user)).thenReturn(sampleUser(123L, "test"));
    when(mockUserRepository.findByNickName("test")).thenReturn(empty());

    var result = service.createUser(user);

    verify(mockUserRepository).findByNickName("test");
    verify(mockUserRepository).save(user);
    assertThat(result.getNickName(), is("test"));
    assertThat(result.getId(), is(123L));
  }


  @Test
  void createUserWithSameNickName() {
    var user = sampleUser("test");
    when(mockUserRepository.save(user)).thenReturn(sampleUser(123L, "test"));
    when(mockUserRepository.findByNickName("test")).thenReturn(of(sampleUser(234L, "test")));

    assertThrows(UserAlreadyExistsException.class, () -> service.createUser(user));
  }

  @Test
  void findUserIfNotPresent() {
    when(mockUserRepository.findById(123L)).thenReturn(empty());

    var actualUser = service.findUserById(123L);

    verify(mockUserRepository).findById(123L);
    assertThat(actualUser.isEmpty(), is(true));
  }

  @Test
  void findUser() {
    when(mockUserRepository.findById(123L)).thenReturn(of(sampleUser(123L, "test")));

    var actualUser = service.findUserById(123L);

    verify(mockUserRepository).findById(123L);
    assertThat(actualUser.isPresent(), is(true));
    assertThat(actualUser.get().getNickName(), is("test"));
    assertThat(actualUser.get().getId(), is(123L));
  }
}