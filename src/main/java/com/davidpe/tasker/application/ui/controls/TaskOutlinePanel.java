package com.davidpe.tasker.application.ui.controls;

import com.davidpe.tasker.domain.task.Task;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Reusable right-side task outline. The host owns navigation and persistence; this control only
 * renders the selected task and exposes Edit/Complete actions.
 */
public class TaskOutlinePanel extends VBox {
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

  private final Label title = new Label("No task selected");
  private final Label status = new Label();
  private final Label project = new Label();
  private final Label priority = new Label();
  private final Label tag = new Label();
  private final Label schedule = new Label();
  private final Label description = new Label("Select a task card to inspect its details.");
  private final Button editButton = new Button("Edit");
  private final Button acceptButton = new Button("Mark complete");

  public TaskOutlinePanel() {
    getStyleClass().add("task-outline-panel");
    setSpacing(14);
    setPadding(new Insets(20));
    setPrefWidth(300);
    setMinWidth(280);
    setMaxWidth(340);

    Label eyebrow = new Label("Outline");
    eyebrow.getStyleClass().add("outline-eyebrow");
    title.getStyleClass().add("outline-title");
    title.setWrapText(true);
    status.getStyleClass().add("outline-status");
    description.getStyleClass().add("outline-description");
    description.setWrapText(true);

    VBox details = new VBox(8,
        detail("Project", project), detail("Priority", priority), detail("Tag", tag), detail("Schedule", schedule));
    details.getStyleClass().add("outline-details");

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);
    editButton.getStyleClass().addAll("outline-action", "message-box-button", "message-box-cancel-button");
    acceptButton.getStyleClass().addAll("outline-action", "message-box-button", "message-box-accept-button");
    HBox actions = new HBox(8, editButton, acceptButton);
    actions.setAlignment(Pos.CENTER_RIGHT);
    getChildren().addAll(eyebrow, title, status, details, description, spacer, actions);
    showEmpty();
  }

  public void setOnEdit(Runnable action) { editButton.setOnAction(event -> run(action)); }
  public void setOnAccept(Runnable action) { acceptButton.setOnAction(event -> run(action)); }

  public void showTask(Task task, String projectName, String priorityName, String tagName) {
    if (task == null) {
      showEmpty();
      return;
    }
    title.setText(value(task.getTitle(), "Untitled task"));
    status.setText(value(task.getTaskStatus().getCode().replace('_', ' '), "Backlog"));
    project.setText(value(projectName, "No project"));
    priority.setText(value(priorityName, "No priority"));
    tag.setText(value(tagName, "No tag"));
    schedule.setText(formatSchedule(task));
    description.setText(value(task.getDescription(), "No description"));
    boolean completed = task.getTaskStatus().getCode().equalsIgnoreCase("done");
    acceptButton.setVisible(!completed);
    acceptButton.setManaged(!completed);
  }

  public void showEmpty() {
    title.setText("No task selected");
    status.setText("");
    project.setText("—");
    priority.setText("—");
    tag.setText("—");
    schedule.setText("—");
    description.setText("Select a task card to inspect its details.");
    acceptButton.setVisible(false);
    acceptButton.setManaged(false);
  }

  private VBox detail(String caption, Label value) {
    Label label = new Label(caption);
    label.getStyleClass().add("outline-detail-caption");
    value.getStyleClass().add("outline-detail-value");
    value.setWrapText(true);
    return new VBox(2, label, value);
  }

  private String formatSchedule(Task task) {
    if (task.getStartAt() == null && task.getEndAt() == null) return "Not scheduled";
    String start = task.getStartAt() == null ? "—" : DATE_FORMAT.format(LocalDateTime.ofInstant(task.getStartAt(), ZoneId.systemDefault()));
    String end = task.getEndAt() == null ? "—" : DATE_FORMAT.format(LocalDateTime.ofInstant(task.getEndAt(), ZoneId.systemDefault()));
    return start + " → " + end;
  }

  private String value(String value, String fallback) { return value == null || value.isBlank() ? fallback : value; }
  private void run(Runnable action) { if (action != null) action.run(); }
}
