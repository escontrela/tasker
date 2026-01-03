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

import java.util.function.BiFunction;

/**
 * Factory class for creating screens in the application
 * using the JavaFX framework. 
 * Its purpose is to encapsulate the creation logic for different types of screens.
 */
public class UiScreenFactory {


    private final UiViewLoader fxmlLoader;
    private final Stage primaryStage;
    private final Map<UiScreenId,UiScreen> cache = new EnumMap<>(UiScreenId.class);


    public UiScreenFactory(Stage primaryStage, UiViewLoader fxmlLoader) {

        this.primaryStage = primaryStage;
        this.fxmlLoader = fxmlLoader;
    } 

    public UiScreen create(UiScreenId id) {
        
        return switch (id) {

            case MAIN ->  cache.computeIfAbsent(id,  _ ->
                createScreen(
                    UiScreenId.MAIN,
                    UiScreenId.MAIN.getResourcePath(),
                    "Main",
                    (supplier, controller) -> new UiPrimaryScreen(id, primaryStage, supplier,
                         (MainSceneController) controller)
                )
            );
            
            case SETTINGS -> cache.computeIfAbsent(id, _ ->  
                createScreen(
                    UiScreenId.SETTINGS,
                    UiScreenId.SETTINGS.getResourcePath(),
                    "Settings",
                    (supplier, controller) -> new UiPrimaryScreen(id, primaryStage, supplier, 
                        (SettingsSceneController) controller)
                )
            );
            
            case NEW_TASK_DIALOG -> cache.computeIfAbsent(id, _ ->  
                createScreen(
                    UiScreenId.NEW_TASK_DIALOG,
                    UiScreenId.NEW_TASK_DIALOG.getResourcePath(),
                    "New task",
                    (supplier, controller) -> new UiModalScreen(id, primaryStage, supplier,
                         (NewTaskPanelController) controller)
                )
            );

            default -> throw new IllegalArgumentException(id.toString());
        };
    }

    private UiScreen createScreen(UiScreenId id, String fxml, String title,
                                BiFunction<Supplier<Scene>, Object, UiScreen> builder) {
        try {

            UiViewContext root=  fxmlLoader.load(fxml); 
            Supplier<Scene> supplier = () -> {

                Scene scene = new Scene(root.root());
                scene.setFill(Color.TRANSPARENT);
                return scene;
            };

            return builder.apply(supplier, root.controller());

        } catch (IOException e) {
            
            throw new RuntimeException("Failed to load " + fxml, e);
        }
    } 

    public void close() {

        primaryStage.close();
    }
}