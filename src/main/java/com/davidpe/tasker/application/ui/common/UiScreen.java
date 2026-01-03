package com.davidpe.tasker.application.ui.common;
 
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class represents a screen in the application.
 * It provides methods to control the screen's visibility and state.
 */
public sealed interface UiScreen permits AbstractUiScreen {

  UiScreenId id();
  Stage stage();          // para casos avanzados
  Scene scene();          // opcional
  void show();
  void hide();
  void reset();           // volver a estado inicial (o recargar)
  boolean isShowing();
  UiScreenController controller(); 
  void showAtPosition(java.awt.Point menuPosition);

  default <T> void setData(T data) {
    
    if (controller() instanceof UiControllerDataAware<?> dataAware) {
      @SuppressWarnings("unchecked")
      UiControllerDataAware<T> typedController = (UiControllerDataAware<T>) dataAware;
      typedController.setData(data);      
    }    
  }

}