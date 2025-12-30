package com.davidpe.tasker.application.ui.common.newer;


import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Supplier;

public final class ModalScreen implements Screen {

  private final ScreenId id = null;
  private final Stage owner = null;
  private final Supplier<Stage> stageSupplier = null; // crea un stage modal
  private Stage cachedModal;
  @Override
  public ScreenId id() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'id'");
  }
  @Override
  public Stage stage() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'stage'");
  }
  @Override
  public Scene scene() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'scene'");
  }
  @Override
  public void show() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'show'");
  }
  @Override
  public void hide() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'hide'");
  }
  @Override
  public void reset() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'reset'");
  }
  @Override
  public boolean isShowing() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'isShowing'");
  }

  // show() hace initOwner(owner), initModality(...)
}