package com.davidpe.tasker.application.ui.common.newer;


import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Supplier;

public final class PrimaryScreen implements Screen {

    private final ScreenId id;
    private final Stage stage;
    private final Supplier<Scene> sceneSupplier;

    private Scene cachedScene;

    public PrimaryScreen(ScreenId id,
                         Stage stage,
                         Supplier<Scene> sceneSupplier) {
        this.id = id;
        this.stage = stage;
        this.sceneSupplier = sceneSupplier;
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
        stage.setScene(scene());
        stage.show();
    }

    @Override
    public void hide() {
        stage.hide();
    }

    @Override
    public void reset() {
        cachedScene = null;
    }

    @Override
    public boolean isShowing() {
        return stage.isShowing();
    }

    @Override
    public Stage stage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stage'");
    }
}