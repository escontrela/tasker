package com.davidpe.tasker.application.mcp.dto;

public enum McpPriority {
  HIGH,
  MEDIUM,
  LOW;

  public static McpPriority fromCode(String code) {
    for (McpPriority value : values()) {
      if (value.name().equalsIgnoreCase(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unsupported priority code: " + code);
  }
}
