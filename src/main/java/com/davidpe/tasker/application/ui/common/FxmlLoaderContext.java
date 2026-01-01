package com.davidpe.tasker.application.ui.common;

import javafx.scene.Parent;
/**
 * Context for FXML loading, containing the root node and the associated controller.
 * It is used to provide the necessary context for FXML loading and controller initialization.
 * The FxmlLoaderContext is created by the FxmlLoader during the loading process.
 */
public record FxmlLoaderContext(Parent root, ScreenController controller) {
}