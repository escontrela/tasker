package com.davidpe.tasker.application.ui.common;

/**
 * Enum for FXML screen paths to manage the application's UI.
 */
public enum ScreenPaths {

    MAIN {

        @Override
        public String getFxmlPath() {
            return "/com/davidpe/tasker/ui/main.fxml";
        }
    },

    NEW_TASK {

        @Override
        public String getFxmlPath() {
            return "/com/davidpe/tasker/ui/new-task.fxml";
        }
    };

    public abstract String getFxmlPath();
}