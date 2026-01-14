package com.davidpe.tasker.application.task;

public record UpdateTaskSequenceCommand(Long taskId, TaskSequenceDirection direction) {}
