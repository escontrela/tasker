package com.davidpe.tasker.application.ui.events;

import com.davidpe.tasker.application.ui.common.UiScreenId;
import java.awt.Point;

public final class WindowMenuTaskOpenedEvent extends WindowEvent {

  private final Long taskId;
  private final Point menuPosition;

  public WindowMenuTaskOpenedEvent(Long taskId, Point menuPosition) {
    super(UiScreenId.MENU_TASK_DIALOG);
    this.taskId = taskId;
    this.menuPosition = menuPosition;
  }

  public Long getTaskId() {
    return taskId;
  }

  public Point getMenuPosition() {
    return menuPosition;
  }
}
