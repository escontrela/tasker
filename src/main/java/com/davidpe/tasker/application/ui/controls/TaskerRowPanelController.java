package com.davidpe.tasker.application.ui.controls;

import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskStatus;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Responsive task card used by the task grid. It emits actions but contains no application logic. */
public class TaskerRowPanelController extends VBox {

  public interface RowActionListener {
    void onDeleteClicked(TaskerRowPanelController source);
    void onEditClicked(TaskerRowPanelController source);
    void onMoveUpClicked(TaskerRowPanelController source);
    void onMoveDownClicked(TaskerRowPanelController source);
    void onOpenClicked(TaskerRowPanelController source);
    void onRowClicked(TaskerRowPanelController source);
    void onRowHovered(TaskerRowPanelController source);
    void onRowExited(TaskerRowPanelController source);
    void onRowDoubleClicked(TaskerRowPanelController source);
    void onRowContextMenuRequested(TaskerRowPanelController source, double screenX, double screenY);
  }

  private final Label titleLabel = new Label();
  private final Label dateLabel = new Label();
  private final Label statusLabel = new Label();
  private final Label tagLabel = new Label();
  private final Label priorityLabel = new Label();
  private final Label agentLabel = new Label();
  private final Button completeButton = new Button("Complete");
  private final Button editButton = new Button("Edit");
  private Long taskId;
  private Task task;
  private String taskStatusCode = TaskStatus.BACKLOG;
  private boolean selected;
  private RowActionListener rowActionListener;

  public TaskerRowPanelController() {
    getStyleClass().add("task-card");
    setSpacing(14);
    setPadding(new Insets(20));
    setPrefWidth(380);
    setMinHeight(188);

    titleLabel.getStyleClass().add("task-card-title");
    titleLabel.setWrapText(true);
    titleLabel.setMaxWidth(Double.MAX_VALUE);
    dateLabel.getStyleClass().add("task-card-meta");
    statusLabel.getStyleClass().add("task-status-pill");
    tagLabel.getStyleClass().add("task-card-tag");
    priorityLabel.getStyleClass().add("task-card-meta");
    agentLabel.getStyleClass().add("task-card-meta");
    HBox metadata = new HBox(8, statusLabel, tagLabel, priorityLabel, agentLabel);
    metadata.setAlignment(Pos.CENTER_LEFT);

    completeButton.getStyleClass().addAll("task-card-action", "primary-button");
    completeButton.setOnAction(event -> { if (rowActionListener != null) rowActionListener.onOpenClicked(this); });
    editButton.getStyleClass().addAll("task-card-action", "secondary-button");
    editButton.setOnAction(event -> { if (rowActionListener != null) rowActionListener.onEditClicked(this); });
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    HBox actions = new HBox(8, spacer, editButton, completeButton);
    actions.setAlignment(Pos.CENTER_RIGHT);
    getChildren().addAll(titleLabel, dateLabel, metadata, actions);

    addEventHandler(MouseEvent.MOUSE_ENTERED, event -> notifyHover());
    addEventHandler(MouseEvent.MOUSE_EXITED, event -> notifyExit());
    addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleMouseClick);
  }

  public void setRowActionListener(RowActionListener listener) { this.rowActionListener = listener; }
  public void setTaskId(Long id) { taskId = id; }
  public Long getTaskId() { return taskId; }
  public Task getTask() { return task; }

  public void setTask(Task task) {
    if (task == null) return;
    this.task = task;
    setTaskId(task.getId());
    setName(task.getTitle());
    if (task.getStartAt() != null) {
      setDate(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(
          LocalDateTime.ofInstant(task.getStartAt(), ZoneId.systemDefault())));
    } else setDate("");
    setTaskStatus(task.getTaskStatus().getCode());
  }

  public void setDate(String value) {
    dateLabel.setText(value == null || value.isBlank() ? "No scheduled date" : value);
  }
  public void setName(String value) { titleLabel.setText(value == null || value.isBlank() ? "Untitled task" : value); }
  public void setOpen(String value) { setTaskStatus("Done".equalsIgnoreCase(value) ? TaskStatus.DONE : TaskStatus.BACKLOG); }

  public void setTaskStatus(String value) {
    taskStatusCode = value == null ? TaskStatus.BACKLOG : value;
    boolean done = isDone();
    statusLabel.setText(taskStatusCode.replace('_', ' '));
    statusLabel.getStyleClass().removeAll("status-backlog", "status-planned", "status-in-progress", "status-done");
    statusLabel.getStyleClass().add("status-" + taskStatusCode.toLowerCase(Locale.ROOT).replace('_', '-'));
    completeButton.setVisible(!done);
    completeButton.setManaged(!done);
  }
  public boolean isDone() { return TaskStatus.DONE.equalsIgnoreCase(taskStatusCode); }

  public void setSelected(boolean value) {
    selected = value;
    if (value && !getStyleClass().contains("selected")) getStyleClass().add("selected");
    if (!value) getStyleClass().remove("selected");
  }
  public boolean isSelected() { return selected; }
  public void setPriority(String value) { priorityLabel.setText(value == null || value.isBlank() ? "" : value); }
  public void setAgent(String value) {
    String text = value == null || value.isBlank() ? "" : "Agent: " + value;
    agentLabel.setText(text);
    agentLabel.setVisible(!text.isBlank());
    agentLabel.setManaged(!text.isBlank());
  }
  public void setTags(String tag1, String tag2) {
    String value = tag1 == null ? "" : tag1;
    if (tag2 != null && !tag2.isBlank()) value = value.isBlank() ? tag2 : value + " · " + tag2;
    tagLabel.setText(value);
    tagLabel.setVisible(!value.isBlank());
    tagLabel.setManaged(!value.isBlank());
  }

  private void handleMouseClick(MouseEvent event) {
    if (event.getButton() == MouseButton.SECONDARY) {
      if (rowActionListener != null) rowActionListener.onRowContextMenuRequested(this, event.getScreenX(), event.getScreenY());
      event.consume();
    } else if (event.getClickCount() == 2) {
      if (rowActionListener != null) rowActionListener.onRowDoubleClicked(this);
    } else if (rowActionListener != null) rowActionListener.onRowClicked(this);
  }
  private void notifyHover() { if (rowActionListener != null) rowActionListener.onRowHovered(this); }
  private void notifyExit() { if (rowActionListener != null) rowActionListener.onRowExited(this); }
}
