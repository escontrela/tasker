package com.davidpe.tasker.bootstrap;

import com.davidpe.tasker.application.ui.common.FxmlView;
import com.davidpe.tasker.application.ui.common.StageManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "com.davidpe.tasker")
public class TaskerApplication extends Application {

    private ConfigurableApplicationContext applicationContext;
    private StageManager stageManager;

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
        if (stageManager != null) {
            stageManager.close();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        
        stageManager = applicationContext.getBean(StageManager.class, primaryStage);
        stageManager.switchScene(FxmlView.MAIN);
    }
}
