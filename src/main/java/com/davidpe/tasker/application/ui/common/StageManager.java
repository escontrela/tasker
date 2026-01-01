package com.davidpe.tasker.application.ui.common;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import org.springframework.stereotype.Component;

@Component
public class StageManager {

    private final Stage primaryStage;
    private final FxmlLoader fxmlLoader;
    private final String applicationTitle;

    public StageManager(FxmlLoader fxmlLoader, Stage primaryStage, String applicationTitle) {

        this.primaryStage = primaryStage;
        this.fxmlLoader = fxmlLoader;
        this.applicationTitle = applicationTitle;
    }

    public void switchScene(final FxmlView view) {
        
        primaryStage.setTitle(applicationTitle);

        Parent rootNode = loadRootNode(view.getFxmlPath());
        Scene scene = new Scene(rootNode);
        String stylesheet = Objects.requireNonNull(getClass().getResource("/com/davidpe/tasker/ui/styles.css"))
                .toExternalForm();

        scene.getStylesheets().add(stylesheet);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void switchToNextScene(final FxmlView view) {
        
        Parent rootNode = loadRootNode(view.getFxmlPath());
        primaryStage.getScene().setRoot(rootNode);
        primaryStage.show();

    }

     public void switchToNextParentScene(final FxmlView view) {
        
        Parent rootNode = loadRootNode(view.getFxmlPath());


        Stage stage = new Stage();
        stage.setScene(new Scene(rootNode));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.showAndWait();

    }

    public void close() {

        primaryStage.close();
    }

    private Parent loadRootNode(String fxmlPath) {

        try {

            return fxmlLoader.load(fxmlPath).root();
        
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}