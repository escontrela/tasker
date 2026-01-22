package com.davidpe.tasker.application.ui.common;

import java.util.Objects;
import java.util.function.Supplier;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class represents a modal screen in the application using javafx framework to create a modal
 * dialog.
 */
public final class UiModalScreen extends AbstractUiScreen {

  private final Stage primaryStage;
  private Stage cachedStage;

  public UiModalScreen(
      UiScreenId id,
      Stage primaryStage,
      Supplier<Scene> stageSupplier,
      UiScreenController controller) {

    super(id, stageSupplier, controller);
    this.primaryStage = primaryStage;
  }

  @Override
  public Stage stage() {

    return ensureStage(null);
  }

  @Override
  public void show() {

    showAtPosition(null);
  }

  @Override
  public void showAtPosition(java.awt.Point menuPosition) {

    Stage modalStage = ensureStage(menuPosition);

    modalStage
        .focusedProperty()
        .addListener(
            (obs, wasFocused, isNowFocused) -> {
              if (!isNowFocused) {
                modalStage.hide();
              }
            });
    modalStage.showAndWait();
  }

  @Override
  public void hide() {

    if (Objects.nonNull(cachedStage)) {

      cachedStage.hide();
    }
  }

  @Override
  public boolean isShowing() {

    return Objects.nonNull(cachedStage) && cachedStage.isShowing();
  }

  private Stage ensureStage(java.awt.Point menuPosition) {

    if (Objects.isNull(cachedStage)) {

      cachedStage = new Stage();
      cachedStage.initStyle(StageStyle.TRANSPARENT);

      // This is important because it makes the stage modal only the first time it is shown
      cachedStage.initModality(Modality.WINDOW_MODAL);
      cachedStage.initOwner(primaryStage);

      bindControllerStage(cachedStage);

      cachedStage.setScene(scene());
    }

    if (Objects.nonNull(menuPosition)) {

      cachedStage.setX(menuPosition.getX());

      cachedStage.setY(menuPosition.getY());
    }

    return cachedStage;
  }
}
