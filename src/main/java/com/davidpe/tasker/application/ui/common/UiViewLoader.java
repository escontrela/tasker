package com.davidpe.tasker.application.ui.common;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

/** 
 * This class is responsible for loading FXML files and their associated controllers.
 * It uses by Spring's ApplicationContext to get the controllers.
 */
@Component
public class UiViewLoader {

    private final ApplicationContext applicationContext;

    public UiViewLoader(ApplicationContext applicationContext) {
 
        this.applicationContext = applicationContext;
    }

    public UiViewContext load(String fxmlPath) throws IOException {

        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
        
            throw new IllegalArgumentException("FXML file not found at path: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(applicationContext::getBean);

        return new UiViewContext(loader.load(),loader.getController());
    }
}