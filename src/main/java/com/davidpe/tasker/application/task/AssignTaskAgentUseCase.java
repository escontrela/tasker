package com.davidpe.tasker.application.task;

import com.davidpe.tasker.domain.agents.AgentRepository;
import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskRepository;
import java.time.Instant;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Assigns or clears the workspace agent responsible for a task. */
@Service
public class AssignTaskAgentUseCase {

  private final TaskRepository taskRepository;
  private final AgentRepository agentRepository;
  private final ApplicationEventPublisher eventPublisher;

  public AssignTaskAgentUseCase(
      TaskRepository taskRepository,
      AgentRepository agentRepository,
      ApplicationEventPublisher eventPublisher) {
    this.taskRepository = taskRepository;
    this.agentRepository = agentRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public Task assign(AssignTaskAgentCommand command) {
    if (command == null || command.taskId() == null) {
      throw new IllegalArgumentException("Task ID is required");
    }
    Task task =
        taskRepository
            .findById(command.taskId())
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    if (command.agentId() != null) {
      agentRepository
          .findById(command.agentId())
          .orElseThrow(() -> new IllegalArgumentException("Agent not found"));
    }
    if (java.util.Objects.equals(task.getAgentId(), command.agentId())) {
      return task;
    }
    Task saved = taskRepository.save(task.withAgent(command.agentId(), Instant.now()));
    eventPublisher.publishEvent(new TaskUpdatedEvent(saved));
    return saved;
  }
}
