package com.davidpe.tasker.application.ui.tasks;

import com.davidpe.tasker.application.task.AddTaskCommand;
import com.davidpe.tasker.application.task.AddTaskUseCase;
import com.davidpe.tasker.application.task.GetTaskCommand;
import com.davidpe.tasker.application.task.GetTaskUseCase;
import com.davidpe.tasker.application.task.TaskCreatedEvent;
import com.davidpe.tasker.application.task.UpdateTaskCommand;
import com.davidpe.tasker.application.task.UpdateTaskUseCase;
import com.davidpe.tasker.application.service.task.TaskService;
import com.davidpe.tasker.domain.task.Task;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

 
/**
 * Presenter for the "New Task" UI dialog.
 *
 * <p>
 * Coordinates between the view and application/domain layers to:
 * <ul>
 *   <li>Load and present lookup data (projects, priorities, tags) to the view.</li>
 *   <li>React to project selection changes by filtering and presenting project-specific tags.</li>
 *   <li>Collect user input, build an AddTaskCommand and delegate task creation to the AddTaskUseCase.</li>
 *   <li>Publish a TaskCreatedEvent when a task is successfully created and instruct the view to close.</li>
 *   <li>Surface creation errors to the view.</li>
 * </ul>
 * </p>
 */
@Component
public class NewTaskPresenter {

    private final AddTaskUseCase addTaskUseCase;
    private final GetTaskUseCase getTaskUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final TaskService taskService;
    private final ApplicationEventPublisher eventPublisher;
    private NewTaskView view;
    private Task originalTask = null;

    public NewTaskPresenter(AddTaskUseCase addTaskUseCase,
                            GetTaskUseCase getTaskUseCase,
                            UpdateTaskUseCase updateTaskUseCase,
                            TaskService taskService,
                            ApplicationEventPublisher eventPublisher) {
        this.addTaskUseCase = addTaskUseCase;
        this.getTaskUseCase = getTaskUseCase;
        this.updateTaskUseCase = updateTaskUseCase;
        this.taskService = taskService;
        this.eventPublisher = eventPublisher;
    }

    public void attach(NewTaskView view) {
        this.view = view;
    }

    public void loadInitialData() {

        view.showProjects(taskService.getProjects());
        view.showPriorities(taskService.getPriorities());
        Long projectId = view.selectedProjectId();
        if (projectId != null) {

            view.showTags(taskService.getTagsForProject(projectId));
        }
    }

    public void onProjectChanged(Long projectId) {

        if (projectId == null) {
            return;
        }
        view.showTags(taskService.getTagsForProject(projectId));
    }

    private boolean isEditing() {

        if (view.getData() == null || view.getData().getOperationType() != NewTaskPanelData.OperationType.EDIT) {
            return false;
        }

        return true;    
    }

    public void onSaveRequested() {

        try {
            view.showError("");
            AddTaskCommand command = new AddTaskCommand(
                    view.selectedProjectId(),
                    view.selectedPriorityId(),
                    view.selectedTagId(),
                    view.externalCodeInput(),
                    view.titleInput(),
                    view.descriptionInput(),
                    view.startDate(),
                    view.endDate());

            if (isEditing()) {
                
                var originalId = originalTask != null ? originalTask.getId() : null;
                if (originalId == null) {
                    throw new IllegalStateException("No original task provided for edit operation");
                }

                UpdateTaskCommand updateCommand = new UpdateTaskCommand(
                        originalId,
                        command.projectId(),
                        command.priorityId(),
                        command.tagId(),
                        command.externalCode(),
                        command.title(),
                        command.description(),
                        command.startDate(),
                        command.endDate());
                var saved = updateTaskUseCase.updateTask(updateCommand);
                eventPublisher.publishEvent(new TaskCreatedEvent(saved));
                view.close();
                
            } else {
                var task = addTaskUseCase.addTask(command);
                eventPublisher.publishEvent(new TaskCreatedEvent(task));
                view.close();
            }
        } catch (Exception ex) {

            view.showError(ex.getMessage());
        }
    }

    public void loadTaskData() {

        Long taskId = view.getData().getTaskId();
        if (taskId == null) {
            throw new IllegalStateException("No task ID available for loading task data");
        }

        var task = getTaskUseCase.getTaskById(new GetTaskCommand(taskId));
        if (task == null) {

            throw new IllegalStateException("Task not found");
        }

       originalTask = task;
       view.populateTaskData(task);
       view.selectPriority(task.getPriorityId());
       view.selectProject(task.getProjectId());
       view.selectTag(task.getTagId());
    }
}
