package com.sayhi.fixtures;

import com.sayhi.user.domain.AppUser;

public class UserFixtures {

  public static AppUser sampleUser(String nickName) {
    return AppUser.builder()
      .nickName(nickName)
      .build();
  }


  public static AppUser sampleUser(long id, String nickName) {
    return AppUser.builder()
      .id(id)
      .nickName(nickName)
      .build();
  }
}
