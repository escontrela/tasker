package com.davidpe.tasker.bootstrap;

import com.davidpe.tasker.application.ui.common.FxmlLoader;
import com.davidpe.tasker.application.ui.common.ScreenFactory;

import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

@Configuration
public class ApplicationConfig {

    private final FxmlLoader fxmlLoader;
    private final String applicationTitle;

    public ApplicationConfig(FxmlLoader fxmlLoader, @Value("${application.title}") String applicationTitle) {

        this.fxmlLoader = fxmlLoader;
        this.applicationTitle = applicationTitle;
    }

    @Bean
    @Lazy
    public ScreenFactory screenFactory(Stage stage) throws IOException {

        return new ScreenFactory(stage, fxmlLoader);
    }
}