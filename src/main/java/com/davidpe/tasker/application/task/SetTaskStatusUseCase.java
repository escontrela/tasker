package com.davidpe.tasker.application.task;

import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskRepository;
import com.davidpe.tasker.domain.task.TaskStatus;
import com.davidpe.tasker.domain.task.TaskStatusRepository;
import com.davidpe.tasker.domain.task.TaskStatusUpdatedEvent;
import java.time.Instant;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetTaskStatusUseCase {

  private final TaskRepository taskRepository;
  private final TaskStatusRepository taskStatusRepository;
  private final ApplicationEventPublisher eventPublisher;

  public SetTaskStatusUseCase(
      TaskRepository taskRepository,
      TaskStatusRepository taskStatusRepository,
      ApplicationEventPublisher eventPublisher) {
    this.taskRepository = taskRepository;
    this.taskStatusRepository = taskStatusRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public Task setStatus(SetTaskStatusCommand command) {
    if (command.taskId() == null) {
      throw new IllegalArgumentException("Task ID is required");
    }
    if (command.statusCode() == null || command.statusCode().isBlank()) {
      throw new IllegalArgumentException("Task status code is required");
    }

    Task existing =
        taskRepository
            .findById(command.taskId())
            .orElseThrow(
                () -> new IllegalArgumentException("Task not found with id: " + command.taskId()));

    TaskStatus targetStatus =
        taskStatusRepository
            .findByCode(command.statusCode())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Task status not found with code: " + command.statusCode()));

    if (existing.getTaskStatus().getId().equals(targetStatus.getId())) {
      return existing;
    }

    Task updated =
        new Task(
            existing.getId(),
            existing.getProjectId(),
            existing.getPriorityId(),
            existing.getTagId(),
            existing.getExternalCode(),
            existing.getTitle(),
            existing.getDescription(),
            existing.getStartAt(),
            existing.getEndAt(),
            existing.getSequence(),
            targetStatus,
            existing.getCreatedAt(),
            Instant.now(),
            existing.getAgentId());

    Task saved = taskRepository.save(updated);
    eventPublisher.publishEvent(new TaskStatusUpdatedEvent(saved));
    return saved;
  }
}
