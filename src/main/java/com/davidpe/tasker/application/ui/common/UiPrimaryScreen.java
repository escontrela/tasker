package com.davidpe.tasker.application.ui.common;

 
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.function.Supplier;

/**
 * This class represents the primary screen in the application using
 * javafx framework to create the main user interface.
 */
public final class UiPrimaryScreen extends AbstractUiScreen {

    private final Stage primaryStage;
    private Parent screenRoot;

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
        // Cache this screen's FXML root before any other primary screen replaces it in the
        // shared Scene. Otherwise MAIN would later resolve to the currently displayed root.
        Parent nextRoot = root();
        Scene activeScene = primaryStage.getScene();
        if (activeScene == null) {
            primaryStage.setScene(scene());
        } else {
            if (activeScene.getRoot() != nextRoot) {
            Scene owningScene = nextRoot.getScene();
            if (owningScene != null && owningScene != activeScene) {
                owningScene.setRoot(new Pane());
            }
            activeScene.setRoot(nextRoot);
            }
        }
        controller().onShow();
        primaryStage.show();
    }

    private Parent root() {
        if (screenRoot == null) {
            screenRoot = scene().getRoot();
        }
        return screenRoot;
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
