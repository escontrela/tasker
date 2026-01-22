package com.davidpe.tasker.application.ui.controls;

import com.davidpe.tasker.domain.task.Task;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class TaskerRowPanelController extends Pane {

  private static final String SELECTED_STYLE_CLASS = "selected";

  @FXML private ImageView imgDelete;

  @FXML private ImageView imgDown;

  @FXML private ImageView imgEdit;

  @FXML private ImageView imgUp;

  @FXML private Label lblName;

  @FXML private Label lblOpen;

  // lblDate and lblPriority removed from UI; keep date parts and paneDate styling instead

  @FXML private Label lblTags1;

  @FXML private Label lblTags2;

  @FXML private Pane paneRow;

  @FXML private Button btDone;

  @FXML private Label lbDonebutton;

  @FXML private Label lblDayNumber;

  @FXML private Label lblMonthAbbrev;

  @FXML private Pane paneDate;

  // Optional attached task id to identify the row externally
  private Long taskId;
  private boolean done;
  private boolean selected;
  // whether we currently have a real date set
  private boolean hasDate = false;

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

    void onRowContextMenuRequested(TaskerRowPanelController source, double screenX, double screenY);
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
    // apply priority styling based on priority id/description from caller
    // callers (e.g. MainSceneController) should call setPriority(description) if available
    lblTags1.setText("");
    lblTags2.setText("");
    if (task.getStartAt() != null) {
      LocalDateTime ldt = LocalDateTime.ofInstant(task.getStartAt(), ZoneId.systemDefault());
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
      String dateText = fmt.format(ldt);
      updateDatePartsFromDateText(dateText);
    }
    setDone(Boolean.TRUE.equals(task.getDone()));
  }

  // Individual setters for external controllers
  public void setDate(String dateText) {
    // lblDate was removed from the UI; keep date parts updated
    updateDatePartsFromDateText(dateText);
  }

  public void setName(String name) {
    lblName.setText(name);
  }

  public void setOpen(String openText) {
    // Delegate to setDone so visibility of btDone is handled consistently
    boolean isDone = "Done".equalsIgnoreCase(openText);
    setDone(isDone);
  }

  public void setDone(boolean done) {
    this.done = done;
    String text = done ? "Done" : "Open";
    // Update status label
    if (lblOpen != null) lblOpen.setText(text);
    // The visible button should only appear when the task is Open (done == false)
    boolean showButton = !done;
    if (btDone != null) {
      btDone.setVisible(showButton);
      btDone.setManaged(showButton);
      if (showButton) {
        // The button's visible label must read "Done"
        // btDone.setText("Done");
      }
    }
    // Also update the label inside the button if present
    if (lbDonebutton != null) {
      lbDonebutton.setText(showButton ? "Done" : "");
    }
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
    // lblPriority was removed from the UI; only apply paneDate styling based on priority
    applyPriorityStyle(priorityText);
  }

  public void setTags(String tag1, String tag2) {
    lblTags1.setText(tag1);
    lblTags2.setText(tag2);
  }

  private void applyPriorityStyle(String priorityText) {
    if (priorityText == null) return;
    String normalized = priorityText.toLowerCase();
    // paneDate should show low-priority color when there's no date; otherwise follow priority
    if (paneDate != null) {
      paneDate
          .getStyleClass()
          .removeAll("date-priority-high", "date-priority-medium", "date-priority-low");
      if (!hasDate) {
        paneDate.getStyleClass().add("date-priority-low");
      } else if (normalized.contains("high")) {
        paneDate.getStyleClass().add("date-priority-high");
      } else if (normalized.contains("medium")) {
        paneDate.getStyleClass().add("date-priority-medium");
      } else {
        paneDate.getStyleClass().add("date-priority-low");
      }
    }
  }

  /**
   * Try to extract day number and month abbreviation from the same date text shown in lblDate.
   * Expected format (by default): yyyy-MM-dd HH:mm. If parsing fails, clear the labels.
   */
  private void updateDatePartsFromDateText(String dateText) {
    if (dateText == null || dateText.isEmpty()) {
      hasDate = false;
      if (lblDayNumber != null) lblDayNumber.setText("?");
      if (lblMonthAbbrev != null) lblMonthAbbrev.setText("");
      if (paneDate != null) {
        paneDate
            .getStyleClass()
            .removeAll("date-priority-high", "date-priority-medium", "date-priority-low");
        paneDate.getStyleClass().add("date-priority-low");
      }
      return;
    }
    try {
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
      LocalDateTime ldt = LocalDateTime.parse(dateText, fmt);
      hasDate = true;
      if (lblDayNumber != null) lblDayNumber.setText(String.valueOf(ldt.getDayOfMonth()));
      if (lblMonthAbbrev != null)
        lblMonthAbbrev.setText(ldt.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
    } catch (DateTimeParseException ex) {
      // If parsing fails, try to extract simple numbers safely (fallback)
      try {
        // attempt to parse yyyy-MM-dd prefix
        String[] parts = dateText.split(" ")[0].split("-");
        if (parts.length >= 3) {
          hasDate = true;
          if (lblDayNumber != null)
            lblDayNumber.setText(String.valueOf(Integer.parseInt(parts[2])));
          if (lblMonthAbbrev != null) {
            int month = Integer.parseInt(parts[1]);
            lblMonthAbbrev.setText(
                java.time.Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault()));
          }
          return;
        }
      } catch (Exception e) {
        // ignore fallback errors
      }
      hasDate = false;
      if (lblDayNumber != null) lblDayNumber.setText("?");
      if (lblMonthAbbrev != null) lblMonthAbbrev.setText("");
      if (paneDate != null) {
        paneDate
            .getStyleClass()
            .removeAll("date-priority-high", "date-priority-medium", "date-priority-low");
        paneDate.getStyleClass().add("date-priority-low");
      }
    }
  }

  private boolean isButtonDoneClicked(ActionEvent event) {
    return event.getSource() == btDone
        || event.getTarget() == btDone
        || event.getTarget() == lbDonebutton;
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
    if (event.getButton() == MouseButton.SECONDARY) {
      if (rowActionListener != null) {
        rowActionListener.onRowContextMenuRequested(this, event.getScreenX(), event.getScreenY());
      }
      event.consume();
      return;
    }
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
      // clicking the open label or the done button/label should produce the same action
      if (rowActionListener != null) rowActionListener.onOpenClicked(this);
    } else if (event.getSource() == paneRow) {
      if (rowActionListener != null) rowActionListener.onRowClicked(this);
    }
  }

  @FXML
  void buttonAction(ActionEvent event) {
    if (isButtonDoneClicked(event)) {
      if (rowActionListener != null) rowActionListener.onOpenClicked(this);
    }
  }
}
