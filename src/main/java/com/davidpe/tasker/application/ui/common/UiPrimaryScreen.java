package com.davidpe.tasker.application.ui.common;

 
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Supplier;

/**
 * This class represents the primary screen in the application using
 * javafx framework to create the main user interface.
 */
public final class UiPrimaryScreen extends AbstractUiScreen {

    private final Stage primaryStage;

    public UiPrimaryScreen(UiScreenId id,
                         Stage primaryStage,
                         Supplier<Scene> sceneSupplier,
                         UiScreenController controller) {
        super(id, sceneSupplier, controller);
        this.primaryStage = primaryStage;
        bindControllerStage(primaryStage);
    }

    @Override
    public void show() {

        primaryStage.setScene(scene());
        primaryStage.show();
    }

    @Override
    public void hide() {

        primaryStage.hide();
    }

    @Override
    public boolean isShowing() {

        return primaryStage.isShowing();
    }

    @Override
    public Stage stage() {

        return primaryStage;
    }
}
