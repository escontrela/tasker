package com.davidpe.tasker.application.ui.controls;

import java.util.Arrays;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

/** A reusable navigation path for primary Tasker screens. */
public final class BreadcrumbBar extends HBox {

  private final ObservableList<BreadcrumbItem> items = FXCollections.observableArrayList();

  public BreadcrumbBar() {
    setAlignment(Pos.CENTER_LEFT);
    setSpacing(8);
    setAccessibleText("Breadcrumb");
    getStyleClass().add("breadcrumb-bar");
    items.addListener((ListChangeListener<BreadcrumbItem>) change -> rebuild());
  }

  public ObservableList<BreadcrumbItem> getItems() {
    return items;
  }

  public void setItems(BreadcrumbItem... items) {
    this.items.setAll(items);
  }

  /** Convenience API for non-interactive paths, useful from future controllers. */
  public void setPath(String... labels) {
    setItems(
        Arrays.stream(labels)
            .filter(Objects::nonNull)
            .map(BreadcrumbItem::current)
            .toArray(BreadcrumbItem[]::new));
  }

  private void rebuild() {
    getChildren().clear();
    for (int index = 0; index < items.size(); index++) {
      if (index > 0) {
        Label separator = new Label("›");
        separator.getStyleClass().add("breadcrumb-separator");
        getChildren().add(separator);
      }

      BreadcrumbItem item = items.get(index);
      Label label = new Label(item.text());
      boolean current = index == items.size() - 1;
      label.getStyleClass().add(current ? "breadcrumb-current" : "breadcrumb-item");
      label.setAccessibleText(item.text());

      if (!current && item.action() != null) {
        label.getStyleClass().add("breadcrumb-link");
        label.setFocusTraversable(true);
        label.setOnMouseClicked(event -> item.action().run());
        label.setOnKeyPressed(
            event -> {
              if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                item.action().run();
                event.consume();
              }
            });
      }
      getChildren().add(label);
    }
  }

  public record BreadcrumbItem(String text, Runnable action) {

    public BreadcrumbItem {
      Objects.requireNonNull(text, "text");
    }

    public static BreadcrumbItem link(String text, Runnable action) {
      return new BreadcrumbItem(text, Objects.requireNonNull(action, "action"));
    }

    public static BreadcrumbItem current(String text) {
      return new BreadcrumbItem(text, null);
    }
  }
}
