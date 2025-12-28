package com.davidpe.tasker.application.ui.common;

public enum FxmlView {
    MAIN(ScreenPaths.MAIN),
    SETTINGS(ScreenPaths.SETTINGS),
    NEW_TASK(ScreenPaths.NEW_TASK);

    private final String fxmlPath;

    FxmlView(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }
}
