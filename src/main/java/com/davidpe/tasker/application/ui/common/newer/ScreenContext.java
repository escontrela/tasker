package com.davidpe.tasker.application.ui.common.newer;

import javafx.stage.Stage;

import com.davidpe.tasker.application.ui.common.ScreenLoader;
 
/**
 * Context for screen management, 
 * containing the primary stage and a loader factory.
 * It is used in the classes ModalScreen and PrimaryScreen
 * to provide the necessary context for screen management.
 */
public record ScreenContext(Stage primaryStage,
                            ScreenLoader loaderFactory
                            ){}