package com.davidpe.tasker.application.task;

import com.davidpe.tasker.domain.task.TaskDeletedEvent;
import com.davidpe.tasker.domain.task.TaskRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class DeleteTaskUseCase {

  private final TaskRepository taskRepository;
  private final ApplicationEventPublisher eventPublisher;

  public DeleteTaskUseCase(
      TaskRepository taskRepository, ApplicationEventPublisher eventPublisher) {

    this.taskRepository = taskRepository;
    this.eventPublisher = eventPublisher;
  }

  public void deleteTask(DeleteTaskCommand command) {

    taskRepository
        .findById(command.taskId())
        .ifPresent(
            task -> {
              taskRepository.deleteById(command.taskId());
              eventPublisher.publishEvent(new TaskDeletedEvent(task));
            });
  }
}
