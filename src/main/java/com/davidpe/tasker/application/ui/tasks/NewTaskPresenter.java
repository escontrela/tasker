package com.davidpe.tasker.application.ui.tasks;

import com.davidpe.tasker.application.task.AddTaskCommand;
import com.davidpe.tasker.application.task.AddTaskUseCase;
import com.davidpe.tasker.application.task.TaskCreatedEvent;
import com.davidpe.tasker.domain.project.ProjectRepository;
import com.davidpe.tasker.domain.task.PriorityRepository;
import com.davidpe.tasker.domain.task.TagRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class NewTaskPresenter {

    private final AddTaskUseCase addTaskUseCase;
    private final ProjectRepository projectRepository;
    private final PriorityRepository priorityRepository;
    private final TagRepository tagRepository;
    private final ApplicationEventPublisher eventPublisher;
    private NewTaskView view;

    public NewTaskPresenter(AddTaskUseCase addTaskUseCase,
                            ProjectRepository projectRepository,
                            PriorityRepository priorityRepository,
                            TagRepository tagRepository,
                            ApplicationEventPublisher eventPublisher) {
        this.addTaskUseCase = addTaskUseCase;
        this.projectRepository = projectRepository;
        this.priorityRepository = priorityRepository;
        this.tagRepository = tagRepository;
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
            var task = addTaskUseCase.addTask(command);
            eventPublisher.publishEvent(new TaskCreatedEvent(task));
            view.close();
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
    }
}
