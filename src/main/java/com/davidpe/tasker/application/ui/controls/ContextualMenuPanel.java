package com.davidpe.tasker.application.ui.controls;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Theme-aware contextual menu rendered inside the current workspace rather than in a Stage. */
public class ContextualMenuPanel extends StackPane {
  private static final double MARGIN = 12;
  private static final double SUBMENU_GAP = 8;
  private final VBox menuCard = new VBox(2);
  private final VBox content = new VBox(2);
  private final VBox submenuCard = new VBox(2);
  private final VBox submenuContent = new VBox(2);

  /** A selectable item displayed in a contextual submenu. */
  public record MenuItem(String text, Runnable action) {}

  public ContextualMenuPanel() {
    getStyleClass().add("context-menu-panel");
    setAlignment(Pos.TOP_LEFT);
    setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    setPickOnBounds(true);
    Button close = new Button("×");
    close.getStyleClass().add("context-menu-close-button");
    close.setOnAction(event -> hide());
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    HBox header = new HBox(spacer, close);
    header.setAlignment(Pos.CENTER_RIGHT);
    menuCard.getChildren().setAll(header, content);
    menuCard.setPadding(new Insets(6));
    // A StackPane otherwise stretches a resizable child to the whole workspace.
    // Keep the menu a compact, content-sized panel regardless of its host screen.
    menuCard.setPrefWidth(230);
    menuCard.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    menuCard.getStyleClass().add("context-menu-card");
    submenuCard.setPadding(new Insets(6));
    submenuCard.setPrefWidth(240);
    submenuCard.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    submenuCard.getStyleClass().addAll("context-menu-card", "context-submenu-card");
    getChildren().addAll(menuCard, submenuCard);
    addEventFilter(MouseEvent.MOUSE_PRESSED, event -> { if (event.getTarget() == this) hide(); });
    setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ESCAPE) hide(); });
    hide();
  }

  public void clearItems() {
    content.getChildren().clear();
    hideSubmenu();
  }

  public void addItem(String text, Runnable action) {
    content.getChildren().add(createItem(text, action, true));
  }

  public void addSubmenu(String text, Runnable action) {
    content.getChildren().add(createItem(text + "  ›", action, false));
  }

  public void showSubmenu(String title, java.util.List<MenuItem> items) {
    submenuContent.getChildren().clear();
    Label heading = new Label(title);
    heading.getStyleClass().add("context-menu-submenu-title");
    submenuContent.getChildren().add(heading);
    for (MenuItem item : items) {
      submenuContent.getChildren().add(createItem(item.text(), item.action(), true));
    }
    submenuCard.getChildren().setAll(submenuContent);
    submenuCard.setVisible(true);
    submenuCard.setManaged(true);
    Platform.runLater(this::positionSubmenu);
  }

  public void addSeparator() { content.getChildren().add(new Separator()); }
  public void showAtScene(double sceneX, double sceneY) {
    Point2D point = sceneToLocal(sceneX, sceneY);
    hideSubmenu();
    setVisible(true); setManaged(true); toFront();
    Platform.runLater(() -> positionMenu(point, true));
  }

  private void positionMenu(Point2D point, boolean retryAfterLayout) {
    if (getParent() != null) {
      getParent().applyCss();
      getParent().layout();
    }
    // During its first display the anchored overlay has not always received its bounds yet.
    // Wait for that layout pulse instead of clamping everything to the top-left corner.
    if (retryAfterLayout && (getWidth() <= 0 || getHeight() <= 0)) {
      Platform.runLater(() -> positionMenu(point, false));
      return;
    }
    menuCard.applyCss();
    double width = menuCard.prefWidth(-1);
    double height = menuCard.prefHeight(width);
    menuCard.setTranslateX(clamp(point.getX(), MARGIN, Math.max(MARGIN, getWidth() - width - MARGIN)));
    menuCard.setTranslateY(clamp(point.getY(), MARGIN, Math.max(MARGIN, getHeight() - height - MARGIN)));
    requestFocus();
  }

  private Button createItem(String text, Runnable action, boolean closeOnAction) {
    Button item = new Button();
    item.getStyleClass().add("context-menu-item");
    item.setMaxWidth(Double.MAX_VALUE);
    Label label = new Label(text);
    label.getStyleClass().add("context-menu-item-text");
    item.setGraphic(label);
    item.setOnAction(
        event -> {
          if (closeOnAction) hide();
          if (action != null) action.run();
        });
    return item;
  }

  private void positionSubmenu() {
    if (!submenuCard.isVisible()) return;
    submenuCard.applyCss();
    double width = submenuCard.prefWidth(-1);
    double height = submenuCard.prefHeight(width);
    double preferredX = menuCard.getTranslateX() + menuCard.prefWidth(-1) + SUBMENU_GAP;
    if (preferredX + width + MARGIN > getWidth()) {
      preferredX = menuCard.getTranslateX() - width - SUBMENU_GAP;
    }
    submenuCard.setTranslateX(clamp(preferredX, MARGIN, Math.max(MARGIN, getWidth() - width - MARGIN)));
    submenuCard.setTranslateY(
        clamp(menuCard.getTranslateY() + 30, MARGIN, Math.max(MARGIN, getHeight() - height - MARGIN)));
  }

  private void hideSubmenu() {
    submenuCard.setVisible(false);
    submenuCard.setManaged(false);
  }

  public void hide() {
    hideSubmenu();
    setVisible(false);
    setManaged(false);
  }
  private double clamp(double value, double min, double max) { return Math.max(min, Math.min(value, max)); }
}
