/**
 * Domain event published when a Task is created.
 *
 * <p>Encapsulates the created {@code Task} instance and leverages {@code DomainEvent}'s common
 * behavior (such as event metadata and lifecycle semantics) so the event can be handled or
 * dispatched by the domain/event infrastructure.
 *
 * <p>Example:
 *
 * <pre>
 * Task task = ...;
 * publish(new TaskCreatedEvent(task));
 * </pre>
 *
 * @see Task
 * @see com.davidpe.tasker.domain.common.DomainEvent
 */
package com.davidpe.tasker.domain.task;

import com.davidpe.tasker.domain.common.DomainEvent;

public class TaskCreatedEvent extends DomainEvent<Task> {

  public TaskCreatedEvent(Task task) {
    super(task);
  }
}
