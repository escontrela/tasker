package com.davidpe.tasker.application.ui.common;


import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * This class represents a modal screen in the application using javafx
 * framework to create a modal dialog.
 */
public final class UiModalScreen implements UiScreen {

  private final UiScreenId id;
  private final Stage primaryStage;
  private final Supplier<Scene> sceneSupplier; // crea un stage modal
  private Stage cachedStage;
  private Scene cachedScene;
  private UiScreenController controller;

  public UiModalScreen(UiScreenId id, Stage primaryStage, Supplier<Scene> stageSupplier, UiScreenController controller) {

    this.id = id;
    this.primaryStage = primaryStage;
    this.sceneSupplier = stageSupplier;
    this.controller = controller;

  }

  @Override
  public UiScreenId id() {
  
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

    showScreen(null);
  }

  public void showScreen(java.awt.Point menuPosition) {

     if (Objects.isNull(cachedStage)){

        cachedStage = new Stage();
        cachedStage.initStyle(StageStyle.TRANSPARENT);
        
        //This is important because it makes the stage modal only the first time it is shown
        cachedStage.initModality(Modality.WINDOW_MODAL);
        cachedStage.initOwner(primaryStage);

        controller.setRootStage(cachedStage);

        Scene modalScene = scene();
        if (menuPosition != null) {
          
          cachedStage.setX(menuPosition.getX());
          cachedStage.setY(menuPosition.getY());
        }
        cachedStage.setScene(modalScene);
        
      }
      
      cachedStage.showAndWait();
  }
  @Override
  public void showAtPosition(java.awt.Point menuPosition) {

    showScreen(menuPosition);
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
  public UiScreenController controller() {

    return controller;  
  }

}