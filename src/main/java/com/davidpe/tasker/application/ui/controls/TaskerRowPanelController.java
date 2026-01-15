package com.davidpe.tasker.application.ui.controls;

import com.davidpe.tasker.domain.task.Task;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class TaskerRowPanelController extends Pane {

  private static final String SELECTED_STYLE_CLASS = "selected";

  @FXML private ImageView imgDelete;

  @FXML private ImageView imgDown;

  @FXML private ImageView imgEdit;

  @FXML private ImageView imgUp;

  @FXML private Label lblDate;

  @FXML private Label lblName;

  @FXML private Label lblOpen;

  @FXML private Label lblPriority;

  @FXML private Label lblTags1;

  @FXML private Label lblTags2;

  @FXML private Pane paneRow;

  // Optional attached task id to identify the row externally
  private Long taskId;
  private boolean done;
  private boolean selected;

  // Listener to handle row actions from outside
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
  }

  private RowActionListener rowActionListener;

  public TaskerRowPanelController() {
    FXMLLoader fxmlLoader =
        new FXMLLoader(
            getClass().getResource("/com/davidpe/tasker/ui/controls/tasker-row-control.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new RuntimeException(
          "No se pudo cargar el FXML: /com/davidpe/tasker/ui/controls/tasker-row-control.fxml", e);
    }
  }

  @FXML
  private void initialize() {
    if (paneRow != null) {
      if (!paneRow.getStyleClass().contains("task-item")) {
        paneRow.getStyleClass().add("task-item");
      }
      paneRow.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onRowMouseClicked);
      paneRow.addEventHandler(MouseEvent.MOUSE_ENTERED, this::onRowMouseEntered);
      paneRow.addEventHandler(MouseEvent.MOUSE_EXITED, this::onRowMouseExited);
    }
  }

  /** Set the listener that will be notified when images or the row are clicked. */
  public void setRowActionListener(RowActionListener listener) {
    this.rowActionListener = listener;
  }

  public void setTaskId(Long id) {
    this.taskId = id;
  }

  public Long getTaskId() {
    return taskId;
  }

  // Convenience: set all visible fields from a domain Task
  public void setTask(Task task) {
    if (task == null) return;
    setTaskId(task.getId());
    lblName.setText(task.getTitle());
    setPriority(String.valueOf(task.getPriorityId()));
    lblTags1.setText("");
    lblTags2.setText("");
    if (task.getStartAt() != null) {
      LocalDateTime ldt = LocalDateTime.ofInstant(task.getStartAt(), ZoneId.systemDefault());
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
      lblDate.setText(fmt.format(ldt));
    }
    setDone(Boolean.TRUE.equals(task.getDone()));
  }

  // Individual setters for external controllers
  public void setDate(String dateText) {
    lblDate.setText(dateText);
  }

  public void setName(String name) {
    lblName.setText(name);
  }

  public void setOpen(String openText) {
    lblOpen.setText(openText);
    done = "Done".equalsIgnoreCase(openText);
  }

  public void setDone(boolean done) {
    this.done = done;
    lblOpen.setText(done ? "Done" : "Open");
  }

  public boolean isDone() {
    return done;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
    if (selected) {
      if (!paneRow.getStyleClass().contains(SELECTED_STYLE_CLASS)) {
        paneRow.getStyleClass().add(SELECTED_STYLE_CLASS);
      }
    } else {
      paneRow.getStyleClass().remove(SELECTED_STYLE_CLASS);
    }
  }

  public boolean isSelected() {
    return selected;
  }

  public void setPriority(String priorityText) {
    lblPriority.setText(priorityText);
    applyPriorityStyle(priorityText);
  }

  public void setTags(String tag1, String tag2) {
    lblTags1.setText(tag1);
    lblTags2.setText(tag2);
  }

  private void applyPriorityStyle(String priorityText) {
    if (priorityText == null) return;
    String normalized = priorityText.toLowerCase();
    lblPriority
        .getStyleClass()
        .removeAll("task-priority-high", "task-priority-medium", "task-priority-low");
    if (normalized.contains("high")) {
      lblPriority.getStyleClass().add("task-priority-high");
    } else if (normalized.contains("medium")) {
      lblPriority.getStyleClass().add("task-priority-medium");
    } else if (normalized.contains("low")) {
      lblPriority.getStyleClass().add("task-priority-low");
    }
  }

  private boolean isImageDownClicked(MouseEvent event) {
    return event.getSource() == imgDown;
  }

  private boolean isImageUpClicked(MouseEvent event) {
    return event.getSource() == imgUp;
  }

  private boolean isImageEditClicked(MouseEvent event) {
    return event.getSource() == imgEdit;
  }

  private boolean isImageDeleteClicked(MouseEvent event) {
    return event.getSource() == imgDelete;
  }

  private boolean isLabelOpenClicked(MouseEvent event) {
    return event.getSource() == lblOpen;
  }

  private void onRowMouseEntered(MouseEvent event) {
    if (rowActionListener != null) rowActionListener.onRowHovered(this);
  }

  private void onRowMouseExited(MouseEvent event) {
    if (rowActionListener != null) rowActionListener.onRowExited(this);
  }

  private void onRowMouseClicked(MouseEvent event) {
    if (event.getTarget() instanceof ImageView || event.getTarget() == lblOpen) {
      return;
    }
    if (event.getClickCount() == 2) {
      if (rowActionListener != null) rowActionListener.onRowDoubleClicked(this);
    } else {
      if (rowActionListener != null) rowActionListener.onRowClicked(this);
    }
  }

  @FXML
  void onMouseClicked(MouseEvent event) {
    if (isImageDeleteClicked(event)) {
      if (rowActionListener != null) rowActionListener.onDeleteClicked(this);
    } else if (isImageEditClicked(event)) {
      if (rowActionListener != null) rowActionListener.onEditClicked(this);
    } else if (isImageUpClicked(event)) {
      if (rowActionListener != null) rowActionListener.onMoveUpClicked(this);
    } else if (isImageDownClicked(event)) {
      if (rowActionListener != null) rowActionListener.onMoveDownClicked(this);
    } else if (isLabelOpenClicked(event)) {
      if (rowActionListener != null) rowActionListener.onOpenClicked(this);
    } else if (event.getSource() == paneRow) {
      if (rowActionListener != null) rowActionListener.onRowClicked(this);
    }
  }
}
