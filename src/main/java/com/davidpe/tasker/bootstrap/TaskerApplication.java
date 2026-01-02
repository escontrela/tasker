package com.davidpe.tasker.bootstrap;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.davidpe.tasker.application.ui.common.ScreenFactory;
import com.davidpe.tasker.application.ui.common.ScreenId;

@SpringBootApplication(scanBasePackages = "com.davidpe.tasker")
public class TaskerApplication extends Application {

    private ConfigurableApplicationContext applicationContext;
    private ScreenFactory screenFactory;

    public static void main(String[] args) {
        
        launch(args);
    }

    @Override
    public void init() {

        SpringApplicationBuilder builder = new SpringApplicationBuilder(TaskerApplication.class);
        applicationContext = builder.headless(false).run();
    }

    @Override
    public void stop() {

        if (applicationContext != null) {
            applicationContext.close();
        }
        if (screenFactory != null) {
            screenFactory.close();
        }
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.initStyle(StageStyle.TRANSPARENT);

        String appTitle = applicationContext.getBean("applicationTitle", String.class);
        primaryStage.setTitle(appTitle);


        screenFactory = applicationContext.getBean(ScreenFactory.class,primaryStage);      
        screenFactory.create(ScreenId.MAIN).show();
    }
}