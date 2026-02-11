package com.davidpe.tasker.application.task;

public record SetTaskStatusCommand(Long taskId, String statusCode) {}
