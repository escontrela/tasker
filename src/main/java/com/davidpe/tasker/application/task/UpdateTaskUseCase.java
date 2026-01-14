package com.davidpe.tasker.application.task;

import com.davidpe.tasker.domain.project.ProjectRepository;
import com.davidpe.tasker.domain.task.PriorityRepository;
import com.davidpe.tasker.domain.task.TagRepository;
import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskRepository;
import com.davidpe.tasker.domain.task.TaskUpdatedEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateTaskUseCase {

  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;
  private final PriorityRepository priorityRepository;
  private final TagRepository tagRepository;
  private final ApplicationEventPublisher eventPublisher;

  public UpdateTaskUseCase(
      TaskRepository taskRepository,
      ProjectRepository projectRepository,
      PriorityRepository priorityRepository,
      TagRepository tagRepository,
      ApplicationEventPublisher eventPublisher) {
    this.taskRepository = taskRepository;
    this.projectRepository = projectRepository;
    this.priorityRepository = priorityRepository;
    this.tagRepository = tagRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public Task updateTask(UpdateTaskCommand command) {

    Task existing = loadExisting(command);
    validateDependencies(command);
    Instant startAt = toInstant(command.startDate());
    Instant endAt = toInstant(command.endDate());
    Instant now = Instant.now();
    Task updated =
        new Task(
            existing.getId(),
            command.projectId(),
            command.priorityId(),
            command.tagId(),
            command.externalCode(),
            command.title(),
            command.description(),
            startAt,
            endAt,
            existing.getCreatedAt(),
            now);
    Task toReturn = taskRepository.save(updated);
    eventPublisher.publishEvent(new TaskUpdatedEvent(toReturn));
    return toReturn;
  }

  private Task loadExisting(UpdateTaskCommand command) {

    if (command.taskId() == null) {
      throw new IllegalArgumentException("Task ID is required");
    }
    return taskRepository
        .findById(command.taskId())
        .orElseThrow(
            () -> new IllegalArgumentException("Task not found with id: " + command.taskId()));
  }

  private void validateDependencies(UpdateTaskCommand command) {

    if (command.projectId() == null) {
      throw new IllegalArgumentException("Project is required");
    }
    if (command.priorityId() == null) {
      throw new IllegalArgumentException("Priority is required");
    }
    if (command.title() == null || command.title().isBlank()) {
      throw new IllegalArgumentException("Title is required");
    }
    if (command.description() == null || command.description().isBlank()) {
      throw new IllegalArgumentException("Description is required");
    }
    projectRepository
        .findById(command.projectId())
        .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    priorityRepository
        .findById(command.priorityId())
        .orElseThrow(() -> new IllegalArgumentException("Priority not found"));
    if (command.tagId() != null) {
      tagRepository
          .findById(command.tagId())
          .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
    }
  }

  private Instant toInstant(LocalDate date) {

    if (date == null) {
      return null;
    }
    return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
  }
}
