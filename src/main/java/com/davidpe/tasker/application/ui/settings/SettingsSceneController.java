package com.davidpe.tasker.application.ui.settings;

import com.davidpe.tasker.application.ui.common.UiControllerDataAware;
import com.davidpe.tasker.application.ui.common.UiScreenController;
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

  @FXML private CheckBox nightModeCheckBox;
  @FXML private Button applyButton;

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
    nightModeCheckBox.selectedProperty().addListener((ignored, oldValue, value) -> refreshDirtyState());
  }

  @Override
  public void onShow() {
    loadPreferences();
  }

  @FXML
  void applySettings() {
    if (applyButton.isDisabled()) return;
    preferences.setNightModeEnabled(nightModeCheckBox.isSelected());
    persistedNightMode = nightModeCheckBox.isSelected();
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
    return new SettingsSceneData(preferences.isNightModeEnabled());
  }

  private void loadPreferences() {
    loading = true;
    persistedNightMode = preferences.isNightModeEnabled();
    nightModeCheckBox.setSelected(persistedNightMode);
    loading = false;
    refreshDirtyState();
  }

  private void refreshDirtyState() {
    if (loading || applyButton == null || nightModeCheckBox == null) return;
    boolean dirty = nightModeCheckBox.isSelected() != persistedNightMode;
    applyButton.setVisible(dirty);
    applyButton.setManaged(dirty);
    applyButton.setDisable(!dirty);
  }
}
