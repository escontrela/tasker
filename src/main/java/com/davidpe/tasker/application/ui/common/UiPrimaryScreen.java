package com.davidpe.tasker.application.ui.common;

 
import javafx.scene.Scene;  
import javafx.stage.Stage;

import java.awt.Point;
import java.util.function.Supplier;

/**
 * This class represents the primary screen in the application using
 * javafx framework to create the main user interface.
 */
public final class UiPrimaryScreen implements UiScreen {

    private final UiScreenId id;
    private final Stage primaryStage;
    private final Supplier<Scene> sceneSupplier;
    private Scene cachedScene;
    private UiScreenController controller;    

    public UiPrimaryScreen(UiScreenId id,
                         Stage primaryStage,
                         Supplier<Scene> sceneSupplier,
                         UiScreenController controller) {
        this.id = id;
        this.primaryStage = primaryStage;
        this.sceneSupplier = sceneSupplier;
        this.controller = controller;
        this.controller.setRootStage(primaryStage);
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
    public void show() {

        primaryStage.setScene(scene());
        primaryStage.show();
    }

    @Override
    public void hide() {
        
        primaryStage.hide();
    }

    @Override
    public void reset() {
        
        controller.resetData();
    }

    @Override
    public boolean isShowing() {
        
        return primaryStage.isShowing();
    }

    @Override
    public Stage stage() {

        return primaryStage; 
    }

    @Override
    public UiScreenController controller() {

        return controller;
    }

    @Override
    public void showAtPosition(Point menuPosition) {
        show();
    }
}