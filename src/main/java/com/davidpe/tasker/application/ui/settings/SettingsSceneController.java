package com.davidpe.tasker.application.ui.settings;

import com.davidpe.tasker.application.ui.common.UiControllerDataAware;
import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.application.ui.controls.BreadcrumbBar;
import com.davidpe.tasker.application.ui.controls.BreadcrumbBar.BreadcrumbItem;
import com.davidpe.tasker.application.ui.events.WindowClosedEvent;
import com.davidpe.tasker.application.ui.theme.ApplicationThemeService;
import com.davidpe.tasker.application.ui.theme.TaskerPreferences;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/** Functional application preferences screen backed by local Java preferences. */
@Component
public class SettingsSceneController extends UiScreenController
    implements UiControllerDataAware<SettingsSceneData> {

  private final ApplicationEventPublisher eventPublisher;
  private final TaskerPreferences preferences;
  private final ApplicationThemeService themeService;
  private boolean loading;
  private boolean persistedNightMode;
  private boolean persistedShowSplash;

  @FXML private CheckBox nightModeCheckBox;
  @FXML private CheckBox showSplashCheckBox;
  @FXML private Button applyButton;
  @FXML private BreadcrumbBar breadcrumbBar;

  @Lazy
  public SettingsSceneController(
      ApplicationEventPublisher eventPublisher,
      TaskerPreferences preferences,
      ApplicationThemeService themeService) {
    this.eventPublisher = eventPublisher;
    this.preferences = preferences;
    this.themeService = themeService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    breadcrumbBar.setItems(
        BreadcrumbItem.link("Tasker", this::backToMain),
        BreadcrumbItem.current("Settings"),
        BreadcrumbItem.current("General"));
    nightModeCheckBox
        .selectedProperty()
        .addListener((ignored, oldValue, value) -> refreshDirtyState());
    showSplashCheckBox
        .selectedProperty()
        .addListener((ignored, oldValue, value) -> refreshDirtyState());
  }

  @Override
  public void onShow() {
    loadPreferences();
  }

  @FXML
  void applySettings() {
    if (applyButton.isDisabled()) return;
    preferences.setNightModeEnabled(nightModeCheckBox.isSelected());
    preferences.setSplashScreenEnabled(showSplashCheckBox.isSelected());
    persistedNightMode = nightModeCheckBox.isSelected();
    persistedShowSplash = showSplashCheckBox.isSelected();
    themeService.refreshRegisteredRoots();
    refreshDirtyState();
  }

  @FXML
  void backToMain() {
    eventPublisher.publishEvent(new WindowClosedEvent(UiScreenId.SETTINGS));
  }

  @Override
  public void resetData() {
    loadPreferences();
  }

  @Override
  public void setData(SettingsSceneData data) {
    // The local preference is the source of truth; retained for existing callers.
  }

  @Override
  public SettingsSceneData getData() {
    return new SettingsSceneData(
        preferences.isNightModeEnabled(), preferences.isSplashScreenEnabled());
  }

  private void loadPreferences() {
    loading = true;
    persistedNightMode = preferences.isNightModeEnabled();
    persistedShowSplash = preferences.isSplashScreenEnabled();
    nightModeCheckBox.setSelected(persistedNightMode);
    showSplashCheckBox.setSelected(persistedShowSplash);
    loading = false;
    refreshDirtyState();
  }

  private void refreshDirtyState() {
    if (loading
        || applyButton == null
        || nightModeCheckBox == null
        || showSplashCheckBox == null) return;
    boolean dirty =
        nightModeCheckBox.isSelected() != persistedNightMode
            || showSplashCheckBox.isSelected() != persistedShowSplash;
    applyButton.setVisible(dirty);
    applyButton.setManaged(dirty);
    applyButton.setDisable(!dirty);
  }
}
