package com.davidpe.tasker.application.ui.common;
 
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class represents a screen in the application.
 * It provides methods to control the screen's visibility and state.
 */
public sealed interface Screen permits PrimaryScreen, ModalScreen{

  ScreenId id();
  Stage stage();          // para casos avanzados
  Scene scene();          // opcional
  void show();
  void hide();
  void reset();           // volver a estado inicial (o recargar)
  boolean isShowing();
  ScreenController controller();
  default <T> void setData(T data) {
    
    if (controller() instanceof ControllerDataAware<?> dataAware) {
      @SuppressWarnings("unchecked")
      ControllerDataAware<T> typedController = (ControllerDataAware<T>) dataAware;
      typedController.setData(data);      
    }    
  }

}
