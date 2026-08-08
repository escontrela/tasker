package com.davidpe.tasker.application.ui.settings;

public record SettingsSceneData(Boolean nightModeEnabled, Boolean splashScreenEnabled) {

  public SettingsSceneData(Boolean nightModeEnabled) {
    this(nightModeEnabled, null);
  }
}
