package com.davidpe.tasker.application.service.task;

import com.davidpe.tasker.domain.project.Project;
import com.davidpe.tasker.domain.project.ProjectRepository;
import com.davidpe.tasker.domain.task.Priority;
import com.davidpe.tasker.domain.task.PriorityRepository;
import com.davidpe.tasker.domain.task.Tag;
import com.davidpe.tasker.domain.task.TagRepository;
import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  private final ProjectRepository projectRepository;
  private final PriorityRepository priorityRepository;
  private final TagRepository tagRepository;
  private final TaskRepository taskRepository;

  public TaskService(
      ProjectRepository projectRepository,
      PriorityRepository priorityRepository,
      TagRepository tagRepository,
      TaskRepository taskRepository) {
    this.projectRepository = projectRepository;
    this.priorityRepository = priorityRepository;
    this.tagRepository = tagRepository;
    this.taskRepository = taskRepository;
  }

  public List<Project> getProjects() {
    return projectRepository.findAll();
  }

  public List<Priority> getPriorities() {
    return priorityRepository.findAll();
  }

  @Cacheable(value = "projects", key = "#userId")
  public List<Project> getProjectsByUserId(Long userId) {

    return projectRepository.findByUserId(userId);
  }

  public List<Tag> getTagsForProject(Long projectId) {
    return tagRepository.findByProjectId(projectId);
  }

  public List<Task> getTasks(Long projectId) {
    if (projectId == null) {
      return taskRepository.findAll();
    }
    return taskRepository.findAllByProjectId(projectId);
  }

  public List<Task> getTasksNotDone(Long projectId) {
    if (projectId == null) {
      return taskRepository.findAllNotDone();
    }
    return taskRepository.findAllNotDoneByProjectId(projectId);
  }

  public Optional<Priority> getPriorityById(Long priorityId) {
    return priorityRepository.findById(priorityId);
  }

  public Optional<Tag> getTagById(Long tagId) {
    return tagRepository.findById(tagId);
  }
}
