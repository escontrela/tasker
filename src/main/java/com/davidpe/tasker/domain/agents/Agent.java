package com.davidpe.tasker.domain.agents;

import java.time.Instant;
import java.util.Objects;

/** A named member of the workspace that can later be assigned to tasks. */
public final class Agent {

  private final Long id;
  private final String code;
  private final String name;
  private final AgentRole role;
  private final Instant createdAt;

  public Agent(Long id, String code, String name, AgentRole role, Instant createdAt) {
    this.id = id;
    this.code = requireText(code, "code");
    this.name = requireText(name, "name");
    this.role = Objects.requireNonNull(role, "role");
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

  public AgentRole getRole() {
    return role;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  private static String requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(field + " is required");
    }
    return value.trim();
  }
}
