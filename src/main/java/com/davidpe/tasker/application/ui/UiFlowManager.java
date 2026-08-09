package com.davidpe.tasker.application.ui;

import com.davidpe.tasker.application.ui.common.UiScreen;
import com.davidpe.tasker.application.ui.common.UiScreenFactory;
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.application.ui.events.WindowClosedEvent;
import com.davidpe.tasker.application.ui.events.WindowEditTaskOpenedEvent;
import com.davidpe.tasker.application.ui.events.WindowMenuTaskOpenedEvent;
import com.davidpe.tasker.application.ui.events.WindowNewTaskOpenedEvent;
import com.davidpe.tasker.application.ui.events.WindowNewProjectOpenedEvent;
import com.davidpe.tasker.application.ui.events.WindowOpenedEvent;
import com.davidpe.tasker.application.ui.menu.MenuTaskData;
import com.davidpe.tasker.application.ui.project.NewProjectPanelData;
import com.davidpe.tasker.application.ui.tasks.NewTaskPanelData;
import javafx.application.Platform;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This class manage the general flow of the main screen at the entire application. At this way, it
 * centralizes the navigation logic when windows are opened or closed and avoid to use the
 * ScreenFactory directly
 */
@Component
@Lazy
public class UiFlowManager {

  private final UiScreenFactory screenFactory;
  private UiScreen currentPrimaryScreen;

  @Lazy
  public UiFlowManager(UiScreenFactory screenFactory) {

    this.screenFactory = screenFactory;
  }

  /** Shows a primary workspace in the shared application window. */
  public void show(UiScreenId screenId) {
    UiScreen screen = screenFactory.create(screenId);
    screen.show();
    currentPrimaryScreen = screen;
  }

  @EventListener
  public void onWindowClosed(WindowClosedEvent ev) {

    Platform.runLater(() -> handleClosed(ev));
  }

  @EventListener
  public void onWindowOpened(WindowOpenedEvent ev) {

    Platform.runLater(() -> handleOpened(ev));
  }

  private void handleClosed(WindowClosedEvent ev) {

    if (ev.screenId() == UiScreenId.SETTINGS
        || ev.screenId() == UiScreenId.STATS
        || ev.screenId() == UiScreenId.AGENTS) {

      UiScreen mainScreen = screenFactory.create(UiScreenId.MAIN);
      mainScreen.show();
      return;
    }
    // añadir más reglas según necesidad
  }

  private void handleOpened(WindowOpenedEvent ev) {}

  @EventListener
  private void onNewTaskOpened(WindowNewTaskOpenedEvent ev) {

    UiScreen newTaskDialog = screenFactory.create(UiScreenId.NEW_TASK_DIALOG);
    newTaskDialog.reset();
    newTaskDialog.setData(new NewTaskPanelData(NewTaskPanelData.OperationType.CREATE, null));
    // Point MENU_POSITION = new Point(120, 110);
    // newTaskDialog.showAtPosition(MENU_POSITION);
    newTaskDialog.show();
  }

  @EventListener
  private void onNewProjectOpened(WindowNewProjectOpenedEvent ev) {

    UiScreen newProjectDialog = screenFactory.create(UiScreenId.NEW_PROJECT_DIALOG);
    newProjectDialog.reset();
    newProjectDialog.setData(
        new NewProjectPanelData(NewProjectPanelData.OperationType.CREATE, null));
    newProjectDialog.show();
  }

  @EventListener
  private void onEditTaskOpened(WindowEditTaskOpenedEvent ev) {

    UiScreen editTaskDialog = screenFactory.create(UiScreenId.NEW_TASK_DIALOG);
    editTaskDialog.reset();
    editTaskDialog.setData(
        new NewTaskPanelData(NewTaskPanelData.OperationType.EDIT, ev.getTaskId()));
    editTaskDialog.show();
  }

  @EventListener
  private void onMenuTaskOpened(WindowMenuTaskOpenedEvent ev) {

    UiScreen menuTaskDialog = screenFactory.create(UiScreenId.MENU_TASK_DIALOG);
    menuTaskDialog.reset();
    menuTaskDialog.setData(new MenuTaskData(ev.getTaskId()));
    menuTaskDialog.showAtPosition(ev.getMenuPosition());
  }
}
