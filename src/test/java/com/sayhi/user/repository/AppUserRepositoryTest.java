package com.sayhi.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static com.sayhi.fixtures.UserFixtures.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
class AppUserRepositoryTest {

  @Autowired AppUserRepository repository;
  @Autowired JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setup() {
    truncateTable();
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {
    "testmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestmtestm1" //251 char length
  })
  void saveWhenInvalidNickName(String nickName) {
    var user = sampleUser(nickName);
    assertThrows(DataIntegrityViolationException.class, () -> repository.save(user));
  }

  @Test
  void saveWhenNickNameAlreadyExists() {
    var user1 = sampleUser("test");
    var user2 = sampleUser("test");
    repository.save(user1);
    assertThrows(DataIntegrityViolationException.class, () -> repository.save(user2));
  }

  @Test
  void save() {
    var user = sampleUser("test");
    var dbUser = repository.save(user);

    assertThat(user.getId(), is(notNullValue()));
    assertThat(user.getNickName(), is(dbUser.getNickName()));
  }

  @Test
  void findByIdIfNotExists() {
    var user = repository.findById(123L);
    assertThat(user, is(Optional.empty()));
  }

  @Test
  void findByIdWhenExists() {
    var expectedUser = repository.save(sampleUser("test"));

    var actualUser = repository.findById(expectedUser.getId());
    assertThat(actualUser.isPresent(), is(true));
    assertThat(expectedUser, is(actualUser.get()));
  }

  @Test
  void findByNickNameIfNotExists() {
    var user = repository.findByNickName("test");
    assertThat(user, is(Optional.empty()));
  }

  @Test
  void findByNickNameWhenExists() {
    var expectedUser = repository.save(sampleUser("test"));

    var actualUser = repository.findByNickName("test");
    assertThat(actualUser.isPresent(), is(true));
    assertThat(expectedUser, is(actualUser.get()));
  }

  void truncateTable() {
    jdbcTemplate.execute("TRUNCATE TABLE app_user");
  }
}