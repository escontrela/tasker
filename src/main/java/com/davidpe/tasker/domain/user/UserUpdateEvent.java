package com.davidpe.tasker.domain.user;

import com.davidpe.tasker.domain.common.DomainEvent;

public class UserUpdateEvent extends DomainEvent<User> {

  public UserUpdateEvent(User user) {

    super(user);
  }
}
