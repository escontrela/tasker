package com.davidpe.tasker.application.ui.common.newer;


import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Objects;
import java.util.function.Supplier;

import com.davidpe.tasker.application.ui.common.ScreenController;

/**
 * This class represents a modal screen in the application using javafx
 * framework to create a modal dialog.
 */
public final class ModalScreen implements Screen {

  private final ScreenId id;
  private final Stage primaryStage;
  private final Supplier<Scene> sceneSupplier; // crea un stage modal
  private Stage cachedStage;
  private Scene cachedScene;
  private ScreenController controller;

  public ModalScreen(ScreenId id, Stage primaryStage, Supplier<Scene> stageSupplier, ScreenController controller) {

    this.id = id;
    this.primaryStage = primaryStage;
    this.sceneSupplier = stageSupplier;
    this.controller = controller;

  }

  @Override
  public ScreenId id() {
  
    return id;
  }
  @Override
  public Stage stage() {

    return primaryStage;
  }
  @Override
  public Scene scene() {

    if (Objects.isNull(cachedScene)) {

      cachedScene = sceneSupplier.get();
    }
    return cachedScene;
  }

  @Override
  public void show() {

     if (Objects.isNull(cachedStage)){

        cachedStage = new Stage();
        
        //This is important because it makes the stage modal only the first time it is shown
        cachedStage.initModality(Modality.WINDOW_MODAL);
        cachedStage.initOwner(primaryStage); 
        
        Scene modalScene = scene(); 
        cachedStage.setScene(modalScene);
      }
      
      cachedStage.showAndWait();
  }

  @Override
  public void hide() {

    cachedStage.hide();
  }

  @Override
  public void reset() {

    controller.resetData();
  }
  @Override
  public boolean isShowing() {

     return cachedStage.isShowing();
  }

  @Override
  public ScreenController controller() {

    return controller;  
  }
}