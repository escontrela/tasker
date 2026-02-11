package com.davidpe.tasker.application.mcp.dto;

import java.util.Locale;

public enum McpTaskStatus {
  BACKLOG("backlog"),
  PLANNED("planned"),
  IN_PROGRESS("in_progress"),
  DONE("done");

  private final String dbCode;

  McpTaskStatus(String dbCode) {
    this.dbCode = dbCode;
  }

  public String dbCode() {
    return dbCode;
  }

  public static McpTaskStatus fromDbCode(String code) {
    for (McpTaskStatus status : values()) {
      if (status.dbCode.equalsIgnoreCase(code)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unsupported task status code: " + code);
  }

  public static McpTaskStatus parse(String rawValue) {
    if (rawValue == null || rawValue.isBlank()) {
      throw new IllegalArgumentException("Task status is required");
    }
    String normalized = rawValue.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    return fromDbCode(normalized);
  }
}
