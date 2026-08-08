package com.davidpe.tasker.application.ui.theme;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import javafx.scene.Parent;
import org.springframework.stereotype.Component;

/** Applies the persisted application appearance to every active JavaFX root. */
@Component
public class ApplicationThemeService {

  public static final String NIGHT_MODE_STYLE_CLASS = "night-mode";

  private final TaskerPreferences preferences;
  private final Set<Parent> registeredRoots =
      Collections.newSetFromMap(new WeakHashMap<>());

  public ApplicationThemeService(TaskerPreferences preferences) {
    this.preferences = preferences;
  }

  public void register(Parent root) {
    if (root == null) {
      return;
    }
    registeredRoots.add(root);
    apply(root);
  }

  public void refreshRegisteredRoots() {
    registeredRoots.forEach(this::apply);
  }

  public boolean isNightModeEnabled() {
    return preferences.isNightModeEnabled();
  }

  private void apply(Parent root) {
    root.getStyleClass().remove(NIGHT_MODE_STYLE_CLASS);
    if (preferences.isNightModeEnabled()) {
      root.getStyleClass().add(NIGHT_MODE_STYLE_CLASS);
    }
  }
}
