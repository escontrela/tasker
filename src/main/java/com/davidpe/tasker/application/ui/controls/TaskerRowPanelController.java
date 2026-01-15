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

  // Listener to handle row actions from outside
  public interface RowActionListener {
    void onDeleteClicked(TaskerRowPanelController source);

    void onEditClicked(TaskerRowPanelController source);

    void onMoveUpClicked(TaskerRowPanelController source);

    void onMoveDownClicked(TaskerRowPanelController source);

    void onRowClicked(TaskerRowPanelController source);
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
  private void initialize() {}

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
    lblPriority.setText(String.valueOf(task.getPriorityId()));
    lblTags1.setText("");
    lblTags2.setText("");
    if (task.getStartAt() != null) {
      LocalDateTime ldt = LocalDateTime.ofInstant(task.getStartAt(), ZoneId.systemDefault());
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
      lblDate.setText(fmt.format(ldt));
    }
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
  }

  public void setPriority(String priorityText) {
    lblPriority.setText(priorityText);
  }

  public void setTags(String tag1, String tag2) {
    lblTags1.setText(tag1);
    lblTags2.setText(tag2);
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
    } else if (event.getSource() == paneRow) {
      if (rowActionListener != null) rowActionListener.onRowClicked(this);
    }
  }
}
