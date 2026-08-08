package com.davidpe.tasker.application.ui.controls;

import com.davidpe.tasker.application.ui.theme.ApplicationThemeService;
import javafx.scene.Parent;

/** Resolves an icon resource for the active Tasker appearance. */
public final class ToolbarIconAssetResolver {

  private ToolbarIconAssetResolver() {}

  public static String resolve(Parent root, String lightResource, String darkResource) {
    if (root != null
        && root.getStyleClass().contains(ApplicationThemeService.NIGHT_MODE_STYLE_CLASS)
        && darkResource != null
        && !darkResource.isBlank()) {
      return darkResource;
    }
    return lightResource;
  }
}
