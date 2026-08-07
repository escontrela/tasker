package com.davidpe.tasker.application.mcp.tool;

import com.davidpe.tasker.application.mcp.dto.CreateTaskMcpRequest;
import com.davidpe.tasker.application.mcp.dto.McpTaskStatus;
import com.davidpe.tasker.application.mcp.dto.TaskMcpDto;
import com.davidpe.tasker.application.mcp.dto.UpdateTaskMcpRequest;
import com.davidpe.tasker.application.mcp.service.TaskMcpService;
import java.util.List;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class TaskMcpTool {

  private final TaskMcpService taskMcpService;

  public TaskMcpTool(TaskMcpService taskMcpService) {
    this.taskMcpService = taskMcpService;
  }

  @Tool(
      name = "createTaskInProject",
      description =
          "Create a task in a project. Parameters: projectId, optional userId, priorityId, optional tagId, externalCode, title, description, optional startAt and endAt")
  public TaskMcpDto createTaskInProject(CreateTaskMcpRequest request) {
    return taskMcpService.createTask(request);
  }

  @Tool(
      name = "getProjectTasksByStatus",
      description =
          "Get all tasks for a project by task status ordered by start date ascending. Parameters: projectId, optional userId, status")
  public List<TaskMcpDto> getProjectTasksByStatus(Long projectId, Long userId, String status) {
    return taskMcpService.getTasksByStatus(projectId, userId, McpTaskStatus.parse(status));
  }

  @Tool(
      name = "transitionTaskStatus",
      description =
          "Transition a task status. Parameters: projectId, optional userId, taskId, targetStatus")
  public TaskMcpDto transitionTaskStatus(Long projectId, Long userId, Long taskId, String targetStatus) {
    return taskMcpService.transitionTaskStatus(
        projectId, userId, taskId, McpTaskStatus.parse(targetStatus));
  }

  @Tool(
      name = "getTaskById",
      description =
          "Get the full data for one task. Parameters: projectId, optional userId, taskId")
  public TaskMcpDto getTaskById(Long projectId, Long userId, Long taskId) {
    return taskMcpService.getTask(projectId, userId, taskId);
  }

  @Tool(
      name = "updateTaskById",
      description =
          "Update a task with new values. Parameters: taskId, projectId, optional userId, priorityId, optional tagId, externalCode, title, description, optional startAt and endAt")
  public TaskMcpDto updateTaskById(UpdateTaskMcpRequest request) {
    return taskMcpService.updateTask(request);
  }
}
