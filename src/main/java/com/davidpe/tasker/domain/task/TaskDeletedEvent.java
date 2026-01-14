/**
 * Domain event indicating that a Task has been deleted.
 *
 * <p>This event represents that a task (identified by its ID) was removed from the domain. It is
 * intended to be published by the Task aggregate when a deletion occurs and consumed by other
 * components (read models, audit/audit-log, notification services) to react to the deletion in an
 * eventually consistent manner.
 *
 * <p>Typical payload for this event includes the task identifier and optional metadata such as the
 * deletion timestamp and the actor that performed the deletion.
 */
package com.davidpe.tasker.domain.task;

import com.davidpe.tasker.domain.common.DomainEvent;

public class TaskDeletedEvent extends DomainEvent<Task> {

  public TaskDeletedEvent(Task task) {
    super(task);
  }
}
