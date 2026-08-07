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
  private final VBox menuCard = new VBox(2);
  private final VBox content = new VBox(2);

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
    menuCard.getStyleClass().add("context-menu-card");
    getChildren().add(menuCard);
    addEventFilter(MouseEvent.MOUSE_PRESSED, event -> { if (event.getTarget() == this) hide(); });
    setOnKeyPressed(event -> { if (event.getCode() == KeyCode.ESCAPE) hide(); });
    hide();
  }

  public void clearItems() { content.getChildren().clear(); }
  public void addItem(String text, Runnable action) {
    Button item = new Button();
    item.getStyleClass().add("context-menu-item");
    item.setMaxWidth(Double.MAX_VALUE);
    Label label = new Label(text);
    label.getStyleClass().add("context-menu-item-text");
    item.setGraphic(label);
    item.setOnAction(event -> { hide(); if (action != null) action.run(); });
    content.getChildren().add(item);
  }
  public void addSeparator() { content.getChildren().add(new Separator()); }
  public void showAtScene(double sceneX, double sceneY) {
    Point2D point = sceneToLocal(sceneX, sceneY);
    setVisible(true); setManaged(true); toFront();
    Platform.runLater(() -> {
      menuCard.applyCss();
      double width = menuCard.prefWidth(-1);
      double height = menuCard.prefHeight(width);
      menuCard.setTranslateX(clamp(point.getX(), MARGIN, Math.max(MARGIN, getWidth() - width - MARGIN)));
      menuCard.setTranslateY(clamp(point.getY(), MARGIN, Math.max(MARGIN, getHeight() - height - MARGIN)));
      requestFocus();
    });
  }
  public void hide() { setVisible(false); setManaged(false); }
  private double clamp(double value, double min, double max) { return Math.max(min, Math.min(value, max)); }
}
