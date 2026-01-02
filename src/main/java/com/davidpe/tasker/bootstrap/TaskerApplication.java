package com.davidpe.tasker.bootstrap;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.davidpe.tasker.application.ui.common.UiScreenFactory;
import com.davidpe.tasker.application.ui.common.UiScreenId;

@SpringBootApplication(scanBasePackages = "com.davidpe.tasker")
public class TaskerApplication extends Application {

    private ConfigurableApplicationContext applicationContext;
    private UiScreenFactory screenFactory;

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

        applicationContext.getBeanFactory().registerSingleton("primaryStage", primaryStage);
    
        screenFactory = applicationContext.getBean(UiScreenFactory.class);        
        //screenFactory = applicationContext.getBean(UiScreenFactory.class,primaryStage);      
        
        screenFactory.create(UiScreenId.MAIN).show();
    }
}