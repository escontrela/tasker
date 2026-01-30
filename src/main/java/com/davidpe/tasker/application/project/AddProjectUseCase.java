package com.davidpe.tasker.application.project;

import com.davidpe.tasker.domain.project.Project;
import com.davidpe.tasker.domain.project.ProjectCreatedEvent;
import com.davidpe.tasker.domain.project.ProjectRepository;
import java.time.Instant;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This use case is responsible for adding a new project to the database. Decouple the project
 * creation from the database interaction.
 */
@Service
public class AddProjectUseCase {

  private final ProjectRepository projectRepository;
  private final ApplicationEventPublisher eventPublisher;

  public AddProjectUseCase(
      ProjectRepository projectRepository, ApplicationEventPublisher eventPublisher) {
    this.projectRepository = projectRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public Project addProject(AddProjectCommand command) {
    validate(command);
    Project project = new Project(null, command.userId(), command.name().trim(), Instant.now());
    Project saved = projectRepository.save(project);
    eventPublisher.publishEvent(new ProjectCreatedEvent(saved));
    return saved;
  }

  private void validate(AddProjectCommand command) {
    if (command.userId() == null) {
      throw new IllegalArgumentException("User is required");
    }
    if (command.name() == null || command.name().isBlank()) {
      throw new IllegalArgumentException("Name is required");
    }
  }
}
