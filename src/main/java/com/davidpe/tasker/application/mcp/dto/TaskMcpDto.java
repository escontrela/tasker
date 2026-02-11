package com.davidpe.tasker.application.mcp.dto;

import java.time.Instant;

public record TaskMcpDto(
    Long id,
    Long projectId,
    Long priorityId,
    McpPriority priority,
    Long tagId,
    String externalCode,
    String title,
    String description,
    Instant startAt,
    Instant endAt,
    Long taskStatusId,
    McpTaskStatus taskStatus) {}
