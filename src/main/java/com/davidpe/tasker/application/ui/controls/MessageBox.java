package com.davidpe.tasker.application.ui.controls;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Reusable in-window confirmation panel, styled by the active Tasker theme. */
public class MessageBox extends StackPane {

  private final Label titleLabel = new Label();
  private final Label messageLabel = new Label();
  private final Button closeButton = new Button("×");
  private final Button cancelButton = new Button("Cancel");
  private final Button acceptButton = new Button("Confirm");
  private Runnable onAccept;
  private Runnable onCancel;

  public MessageBox() {
    getStyleClass().add("message-box-overlay");
    setAlignment(Pos.CENTER);
    setPickOnBounds(true);
    setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    titleLabel.getStyleClass().add("message-box-title");
    messageLabel.getStyleClass().add("message-box-message");
    messageLabel.setWrapText(true);
    messageLabel.setMaxWidth(440);
    closeButton.getStyleClass().add("message-box-close-button");
    closeButton.setOnAction(event -> cancel());
    cancelButton.getStyleClass().addAll("message-box-button", "message-box-cancel-button");
    cancelButton.setOnAction(event -> cancel());
    acceptButton.getStyleClass().addAll("message-box-button", "message-box-accept-button");
    acceptButton.setOnAction(event -> accept());
    Region titleSpacer = new Region();
    HBox.setHgrow(titleSpacer, Priority.ALWAYS);
    HBox heading = new HBox(12, titleLabel, titleSpacer, closeButton);
    heading.setAlignment(Pos.CENTER_LEFT);
    Region actionSpacer = new Region();
    HBox.setHgrow(actionSpacer, Priority.ALWAYS);
    HBox actions = new HBox(10, actionSpacer, cancelButton, acceptButton);
    actions.setAlignment(Pos.CENTER_RIGHT);
    VBox card = new VBox(16, heading, messageLabel, actions);
    card.setPadding(new Insets(24));
    card.setMaxWidth(500);
    card.getStyleClass().add("message-box-card");
    getChildren().add(card);
    hide();
  }

  public void show(String title, String message, String acceptText, Runnable acceptAction) {
    titleLabel.setText(title);
    messageLabel.setText(message);
    acceptButton.setText(acceptText);
    onAccept = acceptAction;
    setVisible(true);
    setManaged(true);
    toFront();
  }
  public void hide() { setVisible(false); setManaged(false); }
  public void setOnCancel(Runnable action) { onCancel = action; }
  private void accept() { hide(); if (onAccept != null) onAccept.run(); }
  private void cancel() { hide(); if (onCancel != null) onCancel.run(); }
}
