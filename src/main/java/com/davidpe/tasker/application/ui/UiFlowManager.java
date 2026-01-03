package com.davidpe.tasker.application.ui;
 

import javafx.application.Platform;
import java.awt.Point;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.davidpe.tasker.application.ui.common.UiScreen;
import com.davidpe.tasker.application.ui.common.UiScreenFactory;
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.application.ui.events.WindowClosedEvent;
import com.davidpe.tasker.application.ui.events.WindowOpenedEvent;
import com.davidpe.tasker.application.ui.tasks.NewTaskPanelData;

/**
 * This class manage the general flow of the main screen at
 * the entire application. At this way, it centralizes the navigation logic
 * when windows are opened or closed and avoid to use the ScreenFactory directly
 */
@Component
@Lazy
public class UiFlowManager {

    private final UiScreenFactory screenFactory;

    @Lazy
    public UiFlowManager(UiScreenFactory screenFactory) {

        this.screenFactory = screenFactory;
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

        if (ev.screenId() == UiScreenId.SETTINGS) {

            UiScreen mainScreen = screenFactory.create(UiScreenId.MAIN);
            mainScreen.show();     
            return;
        }
        // añadir más reglas según necesidad
    }

    private void handleOpened(WindowOpenedEvent ev) {

        if (ev.screenId() == UiScreenId.NEW_TASK_DIALOG) {

            UiScreen newTaskDialog = screenFactory.create(UiScreenId.NEW_TASK_DIALOG);
            newTaskDialog.setData(new NewTaskPanelData("Creating a new operation?"));
            Point MENU_POSITION = new Point(120, 110);
            newTaskDialog.showAtPosition(MENU_POSITION);
        }
    }
}