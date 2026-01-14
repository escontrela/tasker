package com.davidpe.tasker.domain.task;

import com.davidpe.tasker.domain.common.DomainEvent;

public class TaskSequenceUpdatedEvent extends DomainEvent<Task> {

  public TaskSequenceUpdatedEvent(Task task) {
    super(task);
  }
}
