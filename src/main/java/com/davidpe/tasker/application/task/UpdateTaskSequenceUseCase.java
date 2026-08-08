package com.davidpe.tasker.application.task;

import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskRepository;
import com.davidpe.tasker.domain.task.TaskSequenceUpdatedEvent;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateTaskSequenceUseCase {

  private static final BigDecimal STEP = BigDecimal.ONE;
  private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

  private final TaskRepository taskRepository;
  private final ApplicationEventPublisher eventPublisher;

  public UpdateTaskSequenceUseCase(
      TaskRepository taskRepository, ApplicationEventPublisher eventPublisher) {
    this.taskRepository = taskRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public Task updateSequence(UpdateTaskSequenceCommand command) {

    if (command.taskId() == null) {
      throw new IllegalArgumentException("Task ID is required");
    }
    if (command.direction() == null) {
      throw new IllegalArgumentException("Direction is required");
    }

    List<Task> tasks = new ArrayList<>(taskRepository.findAll());
    if (tasks.isEmpty()) {
      throw new IllegalArgumentException("No tasks available to reorder");
    }

    tasks.sort(sequenceComparator());

    Task existing =
        tasks.stream()
            .filter(task -> command.taskId().equals(task.getId()))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("Task not found with id: " + command.taskId()));

    int index = tasks.indexOf(existing);
    BigDecimal newSequence =
        command.direction() == TaskSequenceDirection.UP
            ? calculateSequenceForUp(tasks, index)
            : calculateSequenceForDown(tasks, index);

    if (newSequence == null && existing.getSequence() == null) {

      newSequence = BigDecimal.ZERO;
    }
    if (newSequence != null
        && existing.getSequence() != null
        && newSequence.compareTo(existing.getSequence()) == 0) {
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
            newSequence,
            existing.getTaskStatus(),
            existing.getCreatedAt(),
            Instant.now(),
            existing.getAgentId());
    Task saved = taskRepository.save(updated);
    eventPublisher.publishEvent(new TaskSequenceUpdatedEvent(saved));
    return saved;
  }

  private static Comparator<Task> sequenceComparator() {
    return Comparator.comparing(Task::getSequence, Comparator.nullsLast(Comparator.reverseOrder()))
        .thenComparing(Task::getCreatedAt, Comparator.reverseOrder());
  }

  private BigDecimal calculateSequenceForUp(List<Task> tasks, int index) {
    if (index <= 0) {
      return tasks.get(index).getSequence();
    }
    Task above = tasks.get(index - 1);
    Task aboveAbove = index - 2 >= 0 ? tasks.get(index - 2) : null;

    if (above.getSequence() == null) {
      return aboveAbove != null && aboveAbove.getSequence() != null
          ? aboveAbove.getSequence().subtract(STEP)
          : null;
    }

    BigDecimal upper = aboveAbove != null ? aboveAbove.getSequence() : null;
    BigDecimal lower = above.getSequence();
    return calculateBetween(upper, lower, true);
  }

  private BigDecimal calculateSequenceForDown(List<Task> tasks, int index) {
    if (index >= tasks.size() - 1) {
      return tasks.get(index).getSequence();
    }

    Task below = tasks.get(index + 1);
    Task belowBelow = index + 2 < tasks.size() ? tasks.get(index + 2) : null;

    if (below.getSequence() == null) {
      return null;
    }

    BigDecimal upper = below.getSequence();
    BigDecimal lower = belowBelow != null ? belowBelow.getSequence() : null;
    return calculateBetween(upper, lower, false);
  }

  private BigDecimal calculateBetween(BigDecimal upper, BigDecimal lower, boolean moveUp) {
    if (upper == null) {
      return moveUp ? lower.add(STEP) : lower.subtract(STEP);
    }
    if (lower == null) {
      return upper.subtract(STEP);
    }
    return upper.add(lower).divide(BigDecimal.valueOf(2), MATH_CONTEXT);
  }
}
