/**
 * Domain event emitted when a Task aggregate has been updated.
 *
 * <p>This event carries the Task instance representing the current state of the aggregate after the
 * edit operation. It is intended for use by domain/event handlers and infrastructure components
 * that react to changes in task state (for example, updating read models, sending notifications, or
 * triggering other side effects).
 *
 * <p>Consumers should treat the contained Task as a snapshot of the aggregate at the time the event
 * was published.
 *
 * @see com.davidpe.tasker.domain.common.DomainEvent
 * @see Task
 */
package com.davidpe.tasker.domain.task;

import com.davidpe.tasker.domain.common.DomainEvent;

public class TaskUpdatedEvent extends DomainEvent<Task> {

  public TaskUpdatedEvent(Task task) {
    super(task);
  }
}
