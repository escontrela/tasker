package com.davidpe.tasker.application.ui.common.newer;


import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Supplier;

import com.davidpe.tasker.application.ui.common.ScreenController;

/**
 * This class represents the primary screen in the application using
 * javafx framework to create the main user interface.
 */
public final class PrimaryScreen implements Screen {

    private final ScreenId id;
    private final Stage primaryStage;
    private final Supplier<Scene> sceneSupplier;
    private Scene cachedScene;
    private ScreenController controller;    

    public PrimaryScreen(ScreenId id,
                         Stage primaryStage,
                         Supplier<Scene> sceneSupplier,
                         ScreenController controller) {
        this.id = id;
        this.primaryStage = primaryStage;
        this.sceneSupplier = sceneSupplier;
        this.controller = controller;
    }

    @Override
    public ScreenId id() {

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

        return stage(); 
    }

    @Override
    public ScreenController controller() {

        return controller;
    }
}