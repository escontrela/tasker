package com.davidpe.tasker.application.ui.project;

import com.davidpe.tasker.application.service.task.TaskService;
import com.davidpe.tasker.application.service.user.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Presenter for the "New Project" UI dialog.
 *
 * <p>Coordinates between the view and application/domain layers to:
 *
 * <ul>
 *   <li>Load and present lookup data (projects, priorities, tags) to the view.
 *   <li>React to project selection changes by filtering and presenting project-specific tags.
 *   <li>Collect user input, build an AddTaskCommand and delegate task creation to the
 *       AddTaskUseCase.
 *   <li>Publish a TaskCreatedEvent when a task is successfully created and instruct the view to
 *       close.
 *   <li>Surface creation errors to the view.
 * </ul>
 */
@Component
public class NewProjectPresenter {

  private final TaskService taskService;
  private final ApplicationEventPublisher eventPublisher;
  private UserService userService;
  private NewProjectView view;

  public NewProjectPresenter(
      TaskService taskService, UserService userService, ApplicationEventPublisher eventPublisher) {
    this.taskService = taskService;
    this.userService = userService;
    this.eventPublisher = eventPublisher;
  }

  public void attach(NewProjectView view) {

    this.view = view;
  }

  public void loadInitialData() {}

  private boolean isEditing() {

    if (view.getData() == null
        || view.getData().getOperationType() != NewProjectPanelData.OperationType.EDIT) {
      return false;
    }

    return true;
  }

  public void onSaveRequested() {}

  public void loadProjectData() {}
}
