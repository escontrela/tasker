package com.davidpe.tasker.application.ui.startup;

import com.davidpe.tasker.application.ui.theme.ApplicationThemeService;
import com.davidpe.tasker.application.ui.theme.TaskerPreferences;
import java.util.Objects;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

/** Displays Tasker's branded startup experience before opening the main workspace. */
@Component
public class SplashScreenService {

  private static final Duration MINIMUM_DISPLAY_TIME = Duration.seconds(3);
  private static final String LIGHT_LOGO_RESOURCE = "/com/davidpe/tasker/tasker-logo-light.png";
  private static final String DARK_LOGO_RESOURCE = "/com/davidpe/tasker/tasker-logo-dark.png";
  private static final String STYLESHEET_RESOURCE = "/com/davidpe/tasker/ui/styles.css";

  private final TaskerPreferences preferences;
  private final ApplicationThemeService themeService;

  public SplashScreenService(
      TaskerPreferences preferences, ApplicationThemeService themeService) {
    this.preferences = preferences;
    this.themeService = themeService;
  }

  /** Shows the splash for at least three seconds unless the saved preference disables it. */
  public void showIfEnabled(Runnable afterSplash) {
    Objects.requireNonNull(afterSplash, "afterSplash");
    if (!preferences.isSplashScreenEnabled()) {
      afterSplash.run();
      return;
    }

    Stage splashStage = new Stage(StageStyle.UNDECORATED);
    Parent root = createRoot();
    themeService.register(root);

    Scene scene = new Scene(root, 680, 380);
    scene
        .getStylesheets()
        .add(requiredResource(STYLESHEET_RESOURCE, "stylesheet").toExternalForm());
    splashStage.setScene(scene);
    splashStage.setResizable(false);
    splashStage.centerOnScreen();
    splashStage.show();

    PauseTransition minimumDelay = new PauseTransition(MINIMUM_DISPLAY_TIME);
    minimumDelay.setOnFinished(
        event -> {
          splashStage.close();
          afterSplash.run();
        });
    minimumDelay.play();
  }

  private Parent createRoot() {
    ImageView logo = new ImageView(loadLogo());
    logo.setAccessibleText("Tasker");
    logo.setFitWidth(460);
    logo.setFitHeight(124);
    logo.setPreserveRatio(true);
    logo.setSmooth(true);

    Label tagline = new Label("Plan, prioritise and complete your work");
    tagline.getStyleClass().add("splash-tagline");

    Label loadingStatus = new Label();
    loadingStatus.getStyleClass().add("splash-loading-status");
    startLoadingAnimation(loadingStatus);

    VBox root = new VBox(18, logo, tagline, createLoadingGrid(), loadingStatus);
    root.setAlignment(Pos.CENTER);
    root.getStyleClass().add("splash-shell");
    return root;
  }

  private GridPane createLoadingGrid() {
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(6);
    grid.setVgap(6);
    for (int index = 0; index < 9; index++) {
      Region cell = new Region();
      cell.getStyleClass().add("splash-loader-cell");
      grid.add(cell, index % 3, index / 3);

      FadeTransition pulse = new FadeTransition(Duration.millis(780), cell);
      pulse.setFromValue(0.2);
      pulse.setToValue(1.0);
      pulse.setAutoReverse(true);
      pulse.setCycleCount(Timeline.INDEFINITE);
      pulse.setDelay(Duration.millis(index * 70L));
      pulse.play();
    }
    return grid;
  }

  private void startLoadingAnimation(Label loadingStatus) {
    String message = "Preparing your workspace...";
    Timeline typing = new Timeline();
    for (int index = 0; index <= message.length(); index++) {
      int visibleCharacters = index;
      typing
          .getKeyFrames()
          .add(
              new KeyFrame(
                  Duration.millis(index * 52L),
                  event ->
                      loadingStatus.setText(message.substring(0, visibleCharacters))));
    }
    typing.play();
  }

  private Image loadLogo() {
    String resource =
        themeService.isNightModeEnabled() ? DARK_LOGO_RESOURCE : LIGHT_LOGO_RESOURCE;
    return new Image(requiredResource(resource, "splash logo").toExternalForm());
  }

  private java.net.URL requiredResource(String resource, String description) {
    return Objects.requireNonNull(
        getClass().getResource(resource),
        () -> "Missing " + description + " resource: " + resource);
  }
}
