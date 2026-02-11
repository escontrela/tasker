package com.davidpe.tasker.domain.task;

import com.davidpe.tasker.domain.common.DomainEvent;

public class TaskStatusUpdatedEvent extends DomainEvent<Task> {

  public TaskStatusUpdatedEvent(Task task) {
    super(task);
  }
}
