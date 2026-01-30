/**
 * Domain event published when a Project is created.
 *
 * <p>Encapsulates the created {@code Project} instance and leverages {@code DomainEvent}'s common
 * behavior (such as event metadata and lifecycle semantics) so the event can be handled or
 * dispatched by the domain/event infrastructure.
 *
 * <p>Example:
 *
 * <pre>
 * Project project = ...;
 * publish(new ProjectCreatedEvent(project));
 * </pre>
 *
 * @see Project
 * @see com.davidpe.tasker.domain.common.DomainEvent
 */
package com.davidpe.tasker.domain.project;

import com.davidpe.tasker.domain.common.DomainEvent;

public class ProjectCreatedEvent extends DomainEvent<Project> {

  public ProjectCreatedEvent(Project project) {

    super(project);
  }
}
