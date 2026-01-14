/**
 * Domain event emitted when a Task aggregate has its done state toggled.
 *
 * <p>This event carries the Task instance representing the current state of the aggregate after
 * the done flag has changed. Consumers should treat the contained Task as a snapshot of the
 * aggregate at the time the event was published.
 *
 * @see com.davidpe.tasker.domain.common.DomainEvent
 * @see Task
 */
package com.davidpe.tasker.domain.task;

import com.davidpe.tasker.domain.common.DomainEvent;

public class TaskDoneUpdatedEvent extends DomainEvent<Task> {

  public TaskDoneUpdatedEvent(Task task) {
    super(task);
  }
}
