package com.davidpe.tasker.application.ui.common;

import java.util.Objects;
import java.util.function.Supplier;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * This class represents a modal screen in the application using javafx framework to create a modal
 * dialog.
 */
public final class UiModalScreen extends AbstractUiScreen {

  private final Stage primaryStage;
  private Stage cachedStage;
  private Effect ownerEffectBeforeModal;
  private boolean ownerDimmed;

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
    dimOwner();

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
      cachedStage.addEventHandler(WindowEvent.WINDOW_HIDDEN, ignored -> restoreOwner());
    }

    if (Objects.nonNull(menuPosition)) {

      cachedStage.setX(menuPosition.getX());

      cachedStage.setY(menuPosition.getY());
    }

    return cachedStage;
  }

  /**
   * Separates a transparent modal window from its owner. JavaFX blocks a WINDOW_MODAL owner but
   * does not dim it automatically, which makes dialogs blend into the workspace.
   */
  private void dimOwner() {

    Scene ownerScene = primaryStage.getScene();
    if (ownerScene == null || ownerDimmed) {
      return;
    }

    Node ownerRoot = ownerScene.getRoot();
    ownerEffectBeforeModal = ownerRoot.getEffect();
    ownerRoot.setEffect(new ColorAdjust(0, 0, -0.24, 0));
    ownerDimmed = true;
  }

  private void restoreOwner() {

    if (!ownerDimmed) {
      return;
    }

    Scene ownerScene = primaryStage.getScene();
    if (ownerScene != null) {
      ownerScene.getRoot().setEffect(ownerEffectBeforeModal);
    }
    ownerEffectBeforeModal = null;
    ownerDimmed = false;
  }
}
