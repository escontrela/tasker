package com.davidpe.tasker.application.ui.tasks;

import com.davidpe.tasker.application.task.AddTaskCommand;
import com.davidpe.tasker.application.task.AddTaskUseCase;
import com.davidpe.tasker.application.task.TaskCreatedEvent;
import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskRepository;
import com.davidpe.tasker.domain.project.ProjectRepository;
import com.davidpe.tasker.domain.task.PriorityRepository;
import com.davidpe.tasker.domain.task.TagRepository;
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
    private final ProjectRepository projectRepository;
    private final PriorityRepository priorityRepository;
    private final TagRepository tagRepository;
    private final TaskRepository taskRepository;
    private final ApplicationEventPublisher eventPublisher;
    private NewTaskView view;

    public NewTaskPresenter(AddTaskUseCase addTaskUseCase,
                            ProjectRepository projectRepository,
                            PriorityRepository priorityRepository,
                            TagRepository tagRepository,
                            TaskRepository taskRepository,
                            ApplicationEventPublisher eventPublisher) {
        this.addTaskUseCase = addTaskUseCase;
        this.projectRepository = projectRepository;
        this.priorityRepository = priorityRepository;
        this.tagRepository = tagRepository;
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
    }

    public void attach(NewTaskView view) {
        this.view = view;
    }

    public void loadInitialData() {

        view.showProjects(projectRepository.findAll());
        view.showPriorities(priorityRepository.findAll());
        Long projectId = view.selectedProjectId();
        if (projectId != null) {

            view.showTags(tagRepository.findByProjectId(projectId));
        }
    }

    public void onProjectChanged(Long projectId) {

        if (projectId == null) {
            return;
        }
        view.showTags(tagRepository.findByProjectId(projectId));
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

            if (view.getData() != null && view.getData().getOperationType() == NewTaskPanelData.OperationType.EDIT) {
                // Editing existing task: build updated Task object preserving id and createdAt
                var original = view.getData().getTask();
                if (original == null) {
                    throw new IllegalStateException("No original task provided for edit operation");
                }
                java.time.Instant startAt = toInstant(command.startDate());
                java.time.Instant endAt = toInstant(command.endDate());
                java.time.Instant now = java.time.Instant.now();

                Task updated = new Task(
                        original.getId(),
                        command.projectId(),
                        command.priorityId(),
                        command.tagId(),
                        command.externalCode(),
                        command.title(),
                        command.description(),
                        startAt,
                        endAt,
                        original.getCreatedAt(),
                        now
                );

                var saved = taskRepository.save(updated);
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

    private java.time.Instant toInstant(java.time.LocalDate date) {

        if (date == null) {
            return null;
        }
        return date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
    }
}