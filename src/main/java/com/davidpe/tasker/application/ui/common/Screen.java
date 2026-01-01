package com.davidpe.tasker.application.ui.common;
 
import javafx.scene.Scene;
import javafx.stage.Stage;

public sealed interface Screen permits PrimaryScreen, ModalScreen{

  ScreenId id();
  Stage stage();          // para casos avanzados
  Scene scene();          // opcional
  void show();
  void hide();
  void reset();           // volver a estado inicial (o recargar)
  boolean isShowing();
  ScreenController controller();
}
