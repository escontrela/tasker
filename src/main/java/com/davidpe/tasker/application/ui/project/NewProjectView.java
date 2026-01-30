package com.davidpe.tasker.application.ui.project;

import com.davidpe.tasker.application.ui.common.UiControllerDataAware;
import com.davidpe.tasker.domain.project.Project;

/**
 * View contract for the New Task dialog.
 *
 * <p>Extends {@link UiControllerDataAware} so presenters can inspect the incoming {@link
 * NewTaskPanelData} (CREATE or EDIT) when deciding how to save.
 */
public interface NewProjectView extends UiControllerDataAware<NewProjectPanelData> {

  void close();

  String titleInput();

  void showError(String message);

  void populateProjectData(Project project);
}
