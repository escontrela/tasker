/**
 * Abstract base class for domain events that capture something that happened to a domain entity at
 * a specific point in time.
 *
 * <p>Each DomainEvent instance is immutable: the associated entity reference and the timestamp are
 * set when the event is created and do not change afterwards. Concrete event types should extend
 * this class to add event-specific data or behavior.
 *
 * <p>The timestamp is taken using Instant.now() at construction time, representing the moment the
 * event object was created (typically the moment the domain action occurred or was recorded). Usage
 * notes:
 *
 * <ul>
 *   <li>Subclasses should provide constructors that accept the relevant entity (or a
 *       snapshot/identifier of it) and may expose additional accessors for event-specific
 *       information.
 *   <li>Because the entity reference is stored as a generic type T, callers should document what
 *       representation of the entity the event carries (for example, the entire aggregate, a DTO,
 *       or just an identifier).
 * </ul>
 *
 * @param <T> the type of the entity (or entity representation) associated with this event
 */
package com.davidpe.tasker.domain.common;

import java.time.Instant;

public abstract class DomainEvent<T> {

  private final Instant timestamp;
  private final T entity;

  protected DomainEvent(T entity) {

    this.entity = entity;
    this.timestamp = Instant.now();
  }

  public T entity() {

    return entity;
  }

  public Instant timestamp() {
    return timestamp;
  }
}
