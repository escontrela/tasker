package com.davidpe.tasker.application.ui.common;

/**
 * Enum representing the different screen IDs in the application.
 */
public enum UiScreenId {

    MAIN("/com/davidpe/tasker/ui/main.fxml"),
    SETTINGS("/com/davidpe/tasker/ui/settings.fxml"),
    NEW_TASK_DIALOG("/com/davidpe/tasker/ui/new-task.fxml");

    private final String resourcePath;

    UiScreenId(String resourcePath) {
    
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() {
     
        return resourcePath;
    }
}
