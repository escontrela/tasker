package com.davidpe.tasker.application.ui.common;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

/**
 * Service class to load FXML screens.
 * This class is responsible for loading FXML files and providing the necessary
 * Spring context for the controllers.
 */
@Component
public class ScreenLoader {

    private final ApplicationContext context;

    public ScreenLoader(ApplicationContext context) {

        this.context = context;
    }

    /**
     * This method loads an FXML file and returns the root node, using the
     * Spring context to provide the controller.
     * @param fxmlPath
     * @return
     * @throws IOException
     */
    public Parent load(String fxmlPath) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);
        loader.setLocation(getClass().getResource(fxmlPath));
        return loader.load();
    }
}