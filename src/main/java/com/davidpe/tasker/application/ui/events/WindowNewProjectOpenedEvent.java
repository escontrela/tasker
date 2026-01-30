package com.davidpe.tasker.application.ui.events;

import com.davidpe.tasker.application.ui.common.UiScreenId;

public final class WindowNewProjectOpenedEvent extends WindowEvent {

  public WindowNewProjectOpenedEvent() {

    super(UiScreenId.NEW_PROJECT_DIALOG);
  }
}
