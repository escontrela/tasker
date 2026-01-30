package com.davidpe.tasker.application.ui.project;

import com.davidpe.tasker.application.project.AddProjectCommand;
import com.davidpe.tasker.application.project.AddProjectUseCase;
import com.davidpe.tasker.application.service.task.TaskService;
import com.davidpe.tasker.application.service.user.UserService;
import com.davidpe.tasker.domain.project.Project;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Presenter for the "New Project" UI dialog.
 *
 * <p>Coordinates between the view and application/domain layers to:
 *
 * <ul>
 *   <li>Load and present project data to the view when editing.
 *   <li>Collect user input and delegate project creation to the AddProjectUseCase.
 *   <li>Surface creation errors to the view.
 * </ul>
 */
@Component
public class NewProjectPresenter {

  private final TaskService taskService;
  private final AddProjectUseCase addProjectUseCase;
  private UserService userService;
  private NewProjectView view;

  public NewProjectPresenter(
      TaskService taskService, AddProjectUseCase addProjectUseCase, UserService userService) {
    this.taskService = taskService;
    this.addProjectUseCase = addProjectUseCase;
    this.userService = userService;
  }

  public void attach(NewProjectView view) {

    this.view = view;
  }

  public void loadInitialData() {
    if (view == null) return;
    view.showError("");
  }

  private boolean isEditing() {

    if (view.getData() == null
        || view.getData().getOperationType() != NewProjectPanelData.OperationType.EDIT) {
      return false;
    }

    return true;
  }

  public void onSaveRequested() {
    if (view == null) return;

    try {
      if (isEditing()) {
        return;
      }
      Long userId = userService.getSelectedUser().getId();
      Project project =
          addProjectUseCase.addProject(new AddProjectCommand(userId, view.titleInput()));
      if (project != null) {
        view.close();
      }
    } catch (IllegalArgumentException ex) {
      view.showError(ex.getMessage());
    } catch (Exception ex) {
      view.showError("Error creating project");
    }
  }

  public void loadProjectData() {
    if (!isEditing() || view == null) return;
    Long projectId = view.getData().getProjectId();
    Optional<Project> project =
        taskService.getProjectsByUserId(userService.getSelectedUser().getId()).stream()
            .filter(p -> p.getId().equals(projectId))
            .findFirst();
    project.ifPresent(view::populateProjectData);
  }
}
