package com.davidpe.tasker.application.ui.theme;

import java.util.prefs.Preferences;
import org.springframework.stereotype.Component;

/** Local, user-scoped presentation preferences. Task data remains in SQLite. */
@Component
public class TaskerPreferences {

  private static final String NIGHT_MODE_KEY = "night-mode-enabled";
  private final Preferences preferences = Preferences.userNodeForPackage(TaskerPreferences.class);

  public boolean isNightModeEnabled() {
    return preferences.getBoolean(NIGHT_MODE_KEY, false);
  }

  public void setNightModeEnabled(boolean enabled) {
    preferences.putBoolean(NIGHT_MODE_KEY, enabled);
  }
}
