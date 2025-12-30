package com.davidpe.tasker.application.ui.common.newer;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Supplier;
/*

boolean ok = factory.get(ScreenId.CONFIRM_DIALOG)
                   .asModal(Boolean.class)
                   .showAndWait()
                   .orElse(false);

           Screen screen = factory.get(ScreenId.CONFIRM_DIALOG)
                   .asModal();
                screen.showAndWait();

*/
/*
public final class ScreenFactory {

  private final Stage primaryStage;
  private final Map<ScreenId, ScreenBuilder> builders = new EnumMap<>(ScreenId.class);
  private final Map<ScreenId, Screen> cache = new EnumMap<>(ScreenId.class);

  public ScreenFactory(Stage primaryStage) {

    this.primaryStage = primaryStage;
    builders.put(ScreenId.MAIN, ctx -> ctx.primary("/fxml/home.fxml", "Home"));
    builders.put(ScreenId.SETTINGS, ctx -> ctx.primary("/fxml/settings.fxml", "Settings"));
    builders.put(ScreenId.NEW_TASK_DIALOG, ctx -> ctx.modal("/fxml/confirm.fxml", "Confirm"));
  }

  public Screen get(ScreenId id) {
    return cache.computeIfAbsent(id, _ -> builders.get(id).build(new ScreenContext(primaryStage)));
  }
}*/

import org.springframework.stereotype.Component;
import com.davidpe.tasker.application.ui.common.FxmlLoader;

@Component
public class ScreenFactory {


    private final FxmlLoader fxmlLoader;
    private final Stage primaryStage;

    
    public ScreenFactory(Stage primaryStage, FxmlLoader fxmlLoader) {

        this.primaryStage = primaryStage;
        this.fxmlLoader = fxmlLoader;
    }

    public Screen create(ScreenId id) {
        
        return switch (id) {

            case MAIN -> createPrimary(
                    ScreenId.MAIN,
                    ScreenId.MAIN.getResourcePath(),
                    "Main"
            );
            case SETTINGS -> createPrimary(
                    ScreenId.SETTINGS,
                    ScreenId.SETTINGS.getResourcePath(),
                    "Settings"
            );
            case NEW_TASK_DIALOG -> createPrimary(
                    ScreenId.NEW_TASK_DIALOG,
                    ScreenId.NEW_TASK_DIALOG.getResourcePath(),
                    "New task"
            );
        };
    }

    private Screen createPrimary(ScreenId id, String fxml, String title) {

        Supplier<Scene> supplier = () -> {
            try {
                
                Parent root = fxmlLoader.load(fxml);

                Scene scene = new Scene(root);
                primaryStage.setTitle(title);

                return scene;

            } catch (IOException e) {
                throw new RuntimeException("Failed to load " + fxml, e);
            }
        };

        return new PrimaryScreen(id, primaryStage, supplier);
    }
}