package com.davidpe.tasker.domain.task;

import java.util.Objects;

public class TaskStatus {

  public static final String BACKLOG = "backlog";
  public static final String PLANNED = "planned";
  public static final String IN_PROGRESS = "in_progress";
  public static final String DONE = "done";

  private final Long id;
  private final String code;

  public TaskStatus(Long id, String code) {
    this.id = id;
    this.code = Objects.requireNonNull(code, "code");
  }

  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public boolean isDone() {
    return DONE.equalsIgnoreCase(code);
  }

  @Override
  public String toString() {
    return code;
  }
}
