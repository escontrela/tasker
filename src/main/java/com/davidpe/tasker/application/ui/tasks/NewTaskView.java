package com.davidpe.tasker.application.ui.tasks;

import com.davidpe.tasker.application.ui.common.UiControllerDataAware;
import com.davidpe.tasker.domain.project.Project;
import com.davidpe.tasker.domain.task.Priority;
import com.davidpe.tasker.domain.task.Tag;
import com.davidpe.tasker.domain.task.Task;

import java.time.LocalDate;
import java.util.List;

/**
 * View contract for the New Task dialog.
 *
 * <p>Extends {@link UiControllerDataAware} so presenters can inspect the incoming
 * {@link NewTaskPanelData} (CREATE or EDIT) when deciding how to save.</p>
 */
public interface NewTaskView extends UiControllerDataAware<NewTaskPanelData> {

    void showProjects(List<Project> projects);

    void showPriorities(List<Priority> priorities);

    void showTags(List<Tag> tags);

    Long selectedProjectId();

    Long selectedPriorityId();

    Long selectedTagId();

    String titleInput();

    String descriptionInput();

    String externalCodeInput();

    LocalDate startDate();

    LocalDate endDate();

    void showError(String message);

    void close();

    void populateTaskData(Task task);

    void selectTag(Long tagId);

    void selectPriority(Long priorityId);

    void selectProject(Long projectId);
}
