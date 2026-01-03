package com.davidpe.tasker.application.ui.common;

import java.awt.Point;
import java.util.Objects;
import java.util.function.Supplier;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Base class that centralizes shared behaviour for screens backed by a JavaFX {@link Stage}.
 */
public non-sealed abstract class AbstractUiScreen implements UiScreen {

    private final UiScreenId id;
    private final Supplier<Scene> sceneSupplier;
    private final UiScreenController controller;
    private Scene cachedScene;

    protected AbstractUiScreen(UiScreenId id, Supplier<Scene> sceneSupplier, UiScreenController controller) {
        this.id = Objects.requireNonNull(id, "id");
        this.sceneSupplier = Objects.requireNonNull(sceneSupplier, "sceneSupplier");
        this.controller = Objects.requireNonNull(controller, "controller");
    }

    @Override
    public UiScreenId id() {

        return id;
    }

    @Override
    public Scene scene() {

        if (cachedScene == null) {

            cachedScene = sceneSupplier.get();
        }
        return cachedScene;
    }

    @Override
    public void reset() {

        controller.resetData();
    }

    @Override
    public UiScreenController controller() {

        return controller;
    }

    @Override
    public void showAtPosition(Point menuPosition) {

        show();
    }

    protected void bindControllerStage(Stage stage) {

        controller.setRootStage(stage);
    }
}
