package com.davidpe.tasker.application.task;

import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskDoneUpdatedEvent;
import com.davidpe.tasker.domain.task.TaskRepository;
import java.time.Instant;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetDoneTaskUseCase {

  private final TaskRepository taskRepository;
  private final ApplicationEventPublisher eventPublisher;

  public SetDoneTaskUseCase(TaskRepository taskRepository, ApplicationEventPublisher eventPublisher) {
    this.taskRepository = taskRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public Task toggleDone(SetDoneTaskCommand command) {
    if (command.taskId() == null) {
      throw new IllegalArgumentException("Task ID is required");
    }

    Task existing =
        taskRepository
            .findById(command.taskId())
            .orElseThrow(
                () -> new IllegalArgumentException("Task not found with id: " + command.taskId()));

    Boolean nextDone = Boolean.TRUE.equals(existing.getDone()) ? null : Boolean.TRUE;
    Instant now = Instant.now();
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
            nextDone,
            existing.getCreatedAt(),
            now);

    Task saved = taskRepository.save(updated);
    eventPublisher.publishEvent(new TaskDoneUpdatedEvent(saved));
    return saved;
  }
}
