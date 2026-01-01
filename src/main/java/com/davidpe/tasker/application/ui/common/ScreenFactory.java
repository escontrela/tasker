package com.davidpe.tasker.application.ui.common;

import java.io.IOException; 
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

import com.davidpe.tasker.application.ui.main.MainSceneController;
import com.davidpe.tasker.application.ui.settings.SettingsSceneController;
import com.davidpe.tasker.application.ui.tasks.NewTaskPanelController;

import java.util.function.BiFunction;

/**
 * Factory class for creating screens in the application
 * using the JavaFX framework. 
 * Its purpose is to encapsulate the creation logic for different types of screens.
 */
@Component
public class ScreenFactory {


    private final FxmlLoader fxmlLoader;
    private final Stage primaryStage;
    private final Map<ScreenId,Screen> cache = new EnumMap<>(ScreenId.class);


    public ScreenFactory(Stage primaryStage, FxmlLoader fxmlLoader) {

        this.primaryStage = primaryStage;
        this.fxmlLoader = fxmlLoader;
    } 

    public Screen create(ScreenId id) {
        
        return switch (id) {

            case MAIN ->  cache.computeIfAbsent(id,  _ ->
                createScreen(
                    ScreenId.MAIN,
                    ScreenId.MAIN.getResourcePath(),
                    "Main",
                    (supplier, controller) -> new PrimaryScreen(id, primaryStage, supplier,
                         (MainSceneController) controller)
                )
            );
            
            case SETTINGS -> cache.computeIfAbsent(id, _ ->  
                createScreen(
                    ScreenId.SETTINGS,
                    ScreenId.SETTINGS.getResourcePath(),
                    "Settings",
                    (supplier, controller) -> new PrimaryScreen(id, primaryStage, supplier, 
                        (SettingsSceneController) controller)
                )
            );
            
            case NEW_TASK_DIALOG -> cache.computeIfAbsent(id, _ ->  
                createScreen(
                    ScreenId.NEW_TASK_DIALOG,
                    ScreenId.NEW_TASK_DIALOG.getResourcePath(),
                    "New task",
                    (supplier, controller) -> new ModalScreen(id, primaryStage, supplier,
                         (NewTaskPanelController) controller)
                )
            );

            default -> throw new IllegalArgumentException(id.toString());
        };
    }

    private Screen createScreen(ScreenId id, String fxml, String title,
                                BiFunction<Supplier<Scene>, Object, Screen> builder) {
        try {

            FxmlLoaderContext root = fxmlLoader.load(fxml);
            Supplier<Scene> supplier = () -> {
                Scene scene = new Scene(root.root());
                primaryStage.setTitle(title);
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