package com.davidpe.tasker.bootstrap;

import com.davidpe.tasker.application.ui.common.UiScreenFactory;
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.application.ui.UiFlowManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "com.davidpe.tasker")
@EnableCaching
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
    String appTitle = applicationContext.getBean("applicationTitle", String.class);
    primaryStage.setTitle(appTitle);
    primaryStage.setMinWidth(1024);
    primaryStage.setMinHeight(700);
    primaryStage.setWidth(1180);
    primaryStage.setHeight(760);

    applicationContext.getBeanFactory().registerSingleton("primaryStage", primaryStage);

    screenFactory = applicationContext.getBean(UiScreenFactory.class);
    applicationContext.getBean(UiFlowManager.class).show(UiScreenId.MAIN);
  }
}
