package com.sayhi.user.repository;

import com.sayhi.user.domain.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, Long> {

  Optional<AppUser> findByNickName(String nickName);
}
