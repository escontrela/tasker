package com.davidpe.tasker.application.ui.common.newer;

import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Supplier;

import com.davidpe.tasker.application.ui.common.ScreenLoader;
 
public record ScreenContext(Stage primaryStage,
                            ScreenLoader loaderFactory
                            ){}