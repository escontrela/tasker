package com.davidpe.tasker.application.mcp.service;

import com.davidpe.tasker.application.mcp.dto.CreateTaskMcpRequest;
import com.davidpe.tasker.application.mcp.dto.McpPriority;
import com.davidpe.tasker.application.mcp.dto.McpTaskStatus;
import com.davidpe.tasker.application.mcp.dto.TaskMcpDto;
import com.davidpe.tasker.application.mcp.dto.UpdateTaskMcpRequest;
import com.davidpe.tasker.domain.project.Project;
import com.davidpe.tasker.domain.project.ProjectRepository;
import com.davidpe.tasker.domain.task.PriorityRepository;
import com.davidpe.tasker.domain.task.Tag;
import com.davidpe.tasker.domain.task.TagRepository;
import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskRepository;
import com.davidpe.tasker.domain.task.TaskStatus;
import com.davidpe.tasker.domain.task.TaskStatusRepository;
import com.davidpe.tasker.domain.user.UserRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskMcpService {

  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;
  private final PriorityRepository priorityRepository;
  private final TagRepository tagRepository;
  private final TaskStatusRepository taskStatusRepository;
  private final UserRepository userRepository;

  public TaskMcpService(
      TaskRepository taskRepository,
      ProjectRepository projectRepository,
      PriorityRepository priorityRepository,
      TagRepository tagRepository,
      TaskStatusRepository taskStatusRepository,
      UserRepository userRepository) {
    this.taskRepository = taskRepository;
    this.projectRepository = projectRepository;
    this.priorityRepository = priorityRepository;
    this.tagRepository = tagRepository;
    this.taskStatusRepository = taskStatusRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public TaskMcpDto createTask(CreateTaskMcpRequest request) {
    requireCreateInput(request);
    Project project = validateProjectAccess(request.projectId(), request.userId());
    validatePriority(request.priorityId());
    validateTag(request.tagId(), project.getId());

    TaskStatus defaultStatus =
        taskStatusRepository
            .findByCode(TaskStatus.BACKLOG)
            .orElseThrow(() -> new IllegalStateException("Task status backlog not configured"));

    Task created =
        taskRepository.save(
            Task.newTask(
                request.projectId(),
                request.priorityId(),
                request.tagId(),
                request.externalCode(),
                request.title().trim(),
                request.description().trim(),
                request.startAt(),
                request.endAt(),
                defaultStatus));

    return toDto(created);
  }

  @Transactional(readOnly = true)
  public List<TaskMcpDto> getTasksByStatus(Long projectId, Long userId, McpTaskStatus status) {
    if (projectId == null) {
      throw new IllegalArgumentException("Project ID is required");
    }
    if (status == null) {
      throw new IllegalArgumentException("Task status is required");
    }

    validateProjectAccess(projectId, userId);

    List<Task> tasks;
    if (userId == null) {
      tasks =
          taskRepository.findAllByProjectIdAndStatusCodeOrderByStartAtAsc(projectId, status.dbCode());
    } else {
      tasks =
          taskRepository.findAllByProjectIdAndStatusCodeAndUserIdOrderByStartAtAsc(
              projectId, status.dbCode(), userId);
    }

    return tasks.stream().map(this::toDto).toList();
  }

  @Transactional
  public TaskMcpDto transitionTaskStatus(
      Long projectId, Long userId, Long taskId, McpTaskStatus targetStatus) {
    if (projectId == null) {
      throw new IllegalArgumentException("Project ID is required");
    }
    if (taskId == null) {
      throw new IllegalArgumentException("Task ID is required");
    }
    if (targetStatus == null) {
      throw new IllegalArgumentException("Target status is required");
    }

    Task task = loadTask(projectId, userId, taskId);

    TaskStatus statusEntity =
        taskStatusRepository
            .findByCode(targetStatus.dbCode())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Task status not configured for code: " + targetStatus.dbCode()));

    if (task.getTaskStatus().getId().equals(statusEntity.getId())) {
      return toDto(task);
    }

    Task updated =
        new Task(
            task.getId(),
            task.getProjectId(),
            task.getPriorityId(),
            task.getTagId(),
            task.getExternalCode(),
            task.getTitle(),
            task.getDescription(),
            task.getStartAt(),
            task.getEndAt(),
            task.getSequence(),
            statusEntity,
            task.getCreatedAt(),
            Instant.now(),
            task.getAgentId());

    return toDto(taskRepository.save(updated));
  }

  @Transactional(readOnly = true)
  public TaskMcpDto getTask(Long projectId, Long userId, Long taskId) {
    if (projectId == null) {
      throw new IllegalArgumentException("Project ID is required");
    }
    if (taskId == null) {
      throw new IllegalArgumentException("Task ID is required");
    }

    return toDto(loadTask(projectId, userId, taskId));
  }

  @Transactional
  public TaskMcpDto updateTask(UpdateTaskMcpRequest request) {
    requireUpdateInput(request);
    Project project = validateProjectAccess(request.projectId(), request.userId());
    Task existing = loadTask(project.getId(), request.userId(), request.taskId());

    validatePriority(request.priorityId());
    validateTag(request.tagId(), project.getId());

    Task updated =
        new Task(
            existing.getId(),
            project.getId(),
            request.priorityId(),
            request.tagId(),
            request.externalCode(),
            request.title().trim(),
            request.description().trim(),
            request.startAt(),
            request.endAt(),
            existing.getSequence(),
            existing.getTaskStatus(),
            existing.getCreatedAt(),
            Instant.now(),
            existing.getAgentId());

    return toDto(taskRepository.save(updated));
  }

  private Task loadTask(Long projectId, Long userId, Long taskId) {
    validateProjectAccess(projectId, userId);
    if (userId == null) {
      return taskRepository
          .findByIdAndProjectId(taskId, projectId)
          .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }
    return taskRepository
        .findByIdAndProjectIdAndUserId(taskId, projectId, userId)
        .orElseThrow(() -> new IllegalArgumentException("Task not found"));
  }

  private Project validateProjectAccess(Long projectId, Long userId) {
    if (projectId == null) {
      throw new IllegalArgumentException("Project ID is required");
    }
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));

    if (userId != null) {
      userRepository.findById(userId);
      if (!project.getUserId().equals(userId)) {
        throw new IllegalArgumentException("Project does not belong to provided user");
      }
    }
    return project;
  }

  private void validatePriority(Long priorityId) {
    if (priorityId == null) {
      throw new IllegalArgumentException("Priority ID is required");
    }
    priorityRepository
        .findById(priorityId)
        .orElseThrow(() -> new IllegalArgumentException("Priority not found"));
  }

  private void validateTag(Long tagId, Long projectId) {
    if (tagId == null) {
      return;
    }
    Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new IllegalArgumentException("Tag not found"));
    if (!tag.getProjectId().equals(projectId)) {
      throw new IllegalArgumentException("Tag does not belong to project");
    }
  }

  private void requireCreateInput(CreateTaskMcpRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Create task payload is required");
    }
    if (request.projectId() == null) {
      throw new IllegalArgumentException("Project ID is required");
    }
    if (request.title() == null || request.title().isBlank()) {
      throw new IllegalArgumentException("Title is required");
    }
    if (request.description() == null || request.description().isBlank()) {
      throw new IllegalArgumentException("Description is required");
    }
  }

  private void requireUpdateInput(UpdateTaskMcpRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Update task payload is required");
    }
    if (request.taskId() == null) {
      throw new IllegalArgumentException("Task ID is required");
    }
    if (request.projectId() == null) {
      throw new IllegalArgumentException("Project ID is required");
    }
    if (request.title() == null || request.title().isBlank()) {
      throw new IllegalArgumentException("Title is required");
    }
    if (request.description() == null || request.description().isBlank()) {
      throw new IllegalArgumentException("Description is required");
    }
  }

  private TaskMcpDto toDto(Task task) {
    return new TaskMcpDto(
        task.getId(),
        task.getProjectId(),
        task.getPriorityId(),
        McpPriority.fromCode(resolvePriorityCode(task.getPriorityId())),
        task.getTagId(),
        task.getExternalCode(),
        task.getTitle(),
        task.getDescription(),
        task.getStartAt(),
        task.getEndAt(),
        task.getTaskStatus().getId(),
        McpTaskStatus.fromDbCode(task.getTaskStatus().getCode()));
  }

  private String resolvePriorityCode(Long priorityId) {
    return priorityRepository
        .findById(priorityId)
        .orElseThrow(() -> new IllegalStateException("Priority not found for id: " + priorityId))
        .getCode();
  }
}
