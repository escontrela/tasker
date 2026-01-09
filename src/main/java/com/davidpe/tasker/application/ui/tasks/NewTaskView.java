package com.davidpe.tasker.application.ui.tasks;

import com.davidpe.tasker.domain.project.Project;
import com.davidpe.tasker.domain.task.Priority;
import com.davidpe.tasker.domain.task.Tag;
import java.time.LocalDate;
import java.util.List;

public interface NewTaskView {

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
}
