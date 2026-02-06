package com.davidpe.tasker.application.ui.events;

import com.davidpe.tasker.application.ui.common.UiScreenId;

/** Event published when the menu popup for a task returns a selected action. */
public final class WindowMenuTaskSelectedEvent extends WindowEvent {

  public enum Action {
    EDIT,
    DELETE,
    PRIORITY_UP,
    PRIORITY_DOWN,
    SET_BACKLOG,
    SET_PLANNED,
    SET_IN_PROGRESS,
    SET_DONE
  }

  private final Long taskId;
  private final Action action;

  public WindowMenuTaskSelectedEvent(Long taskId, Action action) {
    super(UiScreenId.MENU_TASK_DIALOG);
    this.taskId = taskId;
    this.action = action;
  }

  public Long getTaskId() {
    return taskId;
  }

  public Action getAction() {
    return action;
  }
}
