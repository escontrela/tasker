package com.davidpe.tasker.application.ui.controls;

import com.davidpe.tasker.application.ui.theme.ApplicationThemeService;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/** A cached, theme-aware, image-only toolbar button suitable for FXML. */
public class ToolbarIconButton extends Button {

  private static final double BUTTON_SIZE = 40.0;
  private static final double ICON_SIZE = 18.0;
  private static final PseudoClass SELECTED_PSEUDO_CLASS =
      PseudoClass.getPseudoClass("toolbar-selected");
  private static final Map<String, Image> IMAGE_CACHE = new ConcurrentHashMap<>();

  private final ImageView iconView = new ImageView();
  private final StringProperty lightIconResource =
      new SimpleStringProperty(this, "lightIconResource", "");
  private final StringProperty darkIconResource =
      new SimpleStringProperty(this, "darkIconResource", "");
  private final StringProperty tooltipText = new SimpleStringProperty(this, "tooltipText", "");
  private final BooleanProperty toggleMode = new SimpleBooleanProperty(this, "toggleMode", false);
  private final BooleanProperty selected = new BooleanPropertyBase(false) {
    @Override protected void invalidated() { pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, get()); }
    @Override public Object getBean() { return ToolbarIconButton.this; }
    @Override public String getName() { return "selected"; }
  };
  private final ListChangeListener<String> rootStyleListener = change -> refreshIcon();
  private final ChangeListener<Parent> sceneRootListener = (ignored, oldRoot, root) -> observeRoot(root);
  private Parent observedRoot;
  private Scene observedScene;

  public ToolbarIconButton() {
    getStyleClass().add("toolbar-icon-button");
    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    setMinSize(BUTTON_SIZE, BUTTON_SIZE);
    setPrefSize(BUTTON_SIZE, BUTTON_SIZE);
    setMaxSize(BUTTON_SIZE, BUTTON_SIZE);
    iconView.setFitWidth(ICON_SIZE);
    iconView.setFitHeight(ICON_SIZE);
    iconView.setPreserveRatio(true);
    iconView.setSmooth(true);
    setGraphic(iconView);
    lightIconResource.addListener((ignored, oldValue, value) -> refreshIcon());
    darkIconResource.addListener((ignored, oldValue, value) -> refreshIcon());
    tooltipText.addListener((ignored, oldValue, value) -> refreshTooltip());
    sceneProperty().addListener((ignored, oldScene, scene) -> observeScene(scene));
  }

  @Override public void fire() {
    if (isDisabled()) return;
    if (isToggleMode()) setSelected(!isSelected());
    super.fire();
  }

  public StringProperty lightIconResourceProperty() { return lightIconResource; }
  public String getLightIconResource() { return lightIconResource.get(); }
  public void setLightIconResource(String value) { lightIconResource.set(value); }
  public StringProperty darkIconResourceProperty() { return darkIconResource; }
  public String getDarkIconResource() { return darkIconResource.get(); }
  public void setDarkIconResource(String value) { darkIconResource.set(value); }
  public StringProperty tooltipTextProperty() { return tooltipText; }
  public String getTooltipText() { return tooltipText.get(); }
  public void setTooltipText(String value) { tooltipText.set(value); }
  public BooleanProperty toggleModeProperty() { return toggleMode; }
  public boolean isToggleMode() { return toggleMode.get(); }
  public void setToggleMode(boolean value) { toggleMode.set(value); }
  public BooleanProperty selectedProperty() { return selected; }
  public boolean isSelected() { return selected.get(); }
  public void setSelected(boolean value) { selected.set(value); }

  private void observeScene(Scene scene) {
    if (observedScene != null) observedScene.rootProperty().removeListener(sceneRootListener);
    observedScene = scene;
    if (scene != null) {
      scene.rootProperty().addListener(sceneRootListener);
      observeRoot(scene.getRoot());
    } else observeRoot(null);
  }

  private void observeRoot(Parent root) {
    if (observedRoot != null) observedRoot.getStyleClass().removeListener(rootStyleListener);
    observedRoot = root;
    if (root != null) root.getStyleClass().addListener(rootStyleListener);
    refreshIcon();
  }

  private void refreshIcon() {
    String resource = ToolbarIconAssetResolver.resolve(observedRoot, getLightIconResource(), getDarkIconResource());
    iconView.setImage(resource == null || resource.isBlank() ? null : IMAGE_CACHE.computeIfAbsent(resource, ToolbarIconButton::loadImage));
    boolean night = observedRoot != null && observedRoot.getStyleClass().contains(ApplicationThemeService.NIGHT_MODE_STYLE_CLASS);
    iconView.setEffect(night ? null : new ColorAdjust(0, 0, -1, 0));
  }

  private void refreshTooltip() { setTooltip(getTooltipText().isBlank() ? null : new Tooltip(getTooltipText())); }
  private static Image loadImage(String path) {
    URL resource = ToolbarIconButton.class.getResource(path);
    if (resource == null) throw new IllegalArgumentException("Toolbar icon resource not found: " + path);
    return new Image(resource.toExternalForm());
  }
}
