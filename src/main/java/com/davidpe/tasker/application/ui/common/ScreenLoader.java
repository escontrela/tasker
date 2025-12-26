package com.davidpe.tasker.application.ui.common;

import javafx.scene.Parent;
import org.springframework.stereotype.Component;

@Component
public class ScreenLoader {

    private final FxmlLoader fxmlLoader;

    public ScreenLoader(FxmlLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }

    public Parent loadMainScreen() {
        return loadView(FxmlView.MAIN);
    }

    private Parent loadView(FxmlView view) {
        try {
            return fxmlLoader.load(view.getFxmlPath());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load view: " + view, e);
        }
    }
}
