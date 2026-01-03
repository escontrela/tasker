package com.davidpe.tasker.application.ui.common;

import java.io.IOException; 
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import com.davidpe.tasker.application.ui.main.MainSceneController;
import com.davidpe.tasker.application.ui.settings.SettingsSceneController;
import com.davidpe.tasker.application.ui.tasks.NewTaskPanelController;

/**
 * Factory class for creating screens in the application
 * using the JavaFX framework. 
 * Its purpose is to encapsulate the creation logic for different types of screens.
 */
public class UiScreenFactory {

    @FunctionalInterface
    private interface ScreenBuilder<T extends UiScreenController> {
        UiScreen build(Stage primaryStage, Supplier<Scene> supplier, T controller);
    }

    private record UiScreenDescriptor<T extends UiScreenController>(
            String fxml,
            ScreenBuilder<T> builder,
            Class<T> controllerType) {
    }

    private static final Map<UiScreenId, UiScreenDescriptor<? extends UiScreenController>> SCREEN_DEFINITIONS =
            Map.of(
                    UiScreenId.MAIN,
                    new UiScreenDescriptor<>(
                            UiScreenId.MAIN.getResourcePath(),
                            (stage, supplier, controller) -> new UiPrimaryScreen(UiScreenId.MAIN, stage,
                                    supplier, controller),
                            MainSceneController.class
                    ),
                    UiScreenId.SETTINGS,
                    new UiScreenDescriptor<>(
                            UiScreenId.SETTINGS.getResourcePath(),
                            (stage, supplier, controller) -> new UiPrimaryScreen(UiScreenId.SETTINGS, stage,
                                    supplier, controller),
                            SettingsSceneController.class
                    ),
                    UiScreenId.NEW_TASK_DIALOG,
                    new UiScreenDescriptor<>(
                            UiScreenId.NEW_TASK_DIALOG.getResourcePath(),
                            (stage, supplier, controller) -> new UiModalScreen(UiScreenId.NEW_TASK_DIALOG, stage,
                                    supplier, controller),
                            NewTaskPanelController.class
                    )
            );

    private final UiViewLoader fxmlLoader;
    private final Stage primaryStage;
    private final Map<UiScreenId, UiScreen> cache = new EnumMap<>(UiScreenId.class);


    public UiScreenFactory(Stage primaryStage, UiViewLoader fxmlLoader) {

        this.primaryStage = primaryStage;
        this.fxmlLoader = fxmlLoader;
    }

    public UiScreen create(UiScreenId id) {

        UiScreenDescriptor<? extends UiScreenController> descriptor = SCREEN_DEFINITIONS.get(id);
        if (descriptor == null) {

            throw new IllegalArgumentException(id.toString());
        }

        return cache.computeIfAbsent(id, ignored -> buildScreen(descriptor));
    }

    private <T extends UiScreenController> UiScreen buildScreen(UiScreenDescriptor<T> descriptor) {
        try {

            UiViewContext root = fxmlLoader.load(descriptor.fxml());
            Supplier<Scene> supplier = () -> {

                Scene scene = new Scene(root.root());
                scene.setFill(Color.TRANSPARENT);
                return scene;
            };

            T controller = descriptor.controllerType().cast(root.controller());
            return descriptor.builder().build(primaryStage, supplier, controller);

        } catch (IOException e) {

            throw new RuntimeException("Failed to load " + descriptor.fxml(), e);
        }
    }

    public void close() {

        primaryStage.close();
    }
}