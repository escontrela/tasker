package com.davidpe.tasker.application.task;

import com.davidpe.tasker.domain.task.Task;

public record TaskCreatedEvent(Task task) {
}
