package com.davidpe.tasker.bootstrap;

import com.davidpe.tasker.application.ui.common.UiViewLoader;
import com.davidpe.tasker.application.ui.common.UiScreenFactory;

import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

@Configuration
public class ApplicationConfig {

    private final UiViewLoader fxmlLoader;
    private final String applicationTitle;

    public ApplicationConfig(UiViewLoader fxmlLoader, @Value("${application.title}") String applicationTitle) {

        this.fxmlLoader = fxmlLoader;
        this.applicationTitle = applicationTitle;
    }

    @Bean
    @Lazy
    public UiScreenFactory screenFactory(Stage stage) throws IOException {

        return new UiScreenFactory(stage, fxmlLoader);
    }

    @Bean
    public String applicationTitle() {

        return this.applicationTitle;
    }
}