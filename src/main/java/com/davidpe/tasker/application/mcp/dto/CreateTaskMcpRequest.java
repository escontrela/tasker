package com.davidpe.tasker.application.mcp.dto;

import java.time.Instant;

public record CreateTaskMcpRequest(
    Long projectId,
    Long userId,
    Long priorityId,
    Long tagId,
    String externalCode,
    String title,
    String description,
    Instant startAt,
    Instant endAt) {}
