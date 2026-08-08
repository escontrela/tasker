package com.davidpe.tasker.domain.agents;

import java.time.Instant;
import java.util.Objects;

/** A role that describes the responsibilities available to a Tasker agent. */
public final class AgentRole {

  private final Long id;
  private final String code;
  private final String name;
  private final Instant createdAt;

  public AgentRole(Long id, String code, String name, Instant createdAt) {
    this.id = id;
    this.code = requireText(code, "code");
    this.name = requireText(name, "name");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
  }

  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  @Override
  public String toString() {
    return name;
  }

  private static String requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(field + " is required");
    }
    return value.trim();
  }
}
