package com.davidpe.tasker.application.ui.controls;

import com.davidpe.tasker.domain.task.Task;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class TaskerTablePanelController extends Pane {

  @FXML private ImageView imgMoveLeft;

  @FXML private ImageView imgMoveRight;

  @FXML private Pane paneTableTask;

  @FXML private Separator sepTasker;

  public TaskerTablePanelController() {
    FXMLLoader fxmlLoader =
        new FXMLLoader(
            getClass().getResource("/com/davidpe/tasker/ui/controls/tasker-table-control.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new RuntimeException(
          "No se pudo cargar el FXML: /com/davidpe/tasker/ui/controls/tasker-table-control.fxml",
          e);
    }
  }

  @FXML
  private void initialize() {}

  // Listener for table-level actions
  public interface TableActionListener {
    void onMoveLeftClicked(TaskerTablePanelController source);

    void onMoveRightClicked(TaskerTablePanelController source);

    void onRowClicked(TaskerTablePanelController source);

    // Row specific actions forwarded
    void onRowDeleteClicked(TaskerRowPanelController row);

    void onRowEditClicked(TaskerRowPanelController row);

    void onRowMoveUpClicked(TaskerRowPanelController row);

    void onRowMoveDownClicked(TaskerRowPanelController row);

    void onRowOpenClicked(TaskerRowPanelController row);
  }

  private TableActionListener tableActionListener;

  // Keep track of added rows
  private final List<TaskerRowPanelController> rows = new ArrayList<>();

  // layout constants
  private static final double ROW_HEIGHT = 37.0;
  private static final double ROW_SPACING = 5.0;
  private static final double TOP_MARGIN = 10.0;

  @FXML
  private void initializeInternal() {}

  /** Register a table-level listener to receive actions coming from the table or child rows. */
  public void setTableActionListener(TableActionListener listener) {
    this.tableActionListener = listener;
  }

  /** Add a row built from a Task instance. */
  public TaskerRowPanelController addRow(Task task) {
    TaskerRowPanelController row = new TaskerRowPanelController();
    row.setTask(task);
    attachRowListener(row);
    // compute position for the new row
    int index = rows.size();
    double y = TOP_MARGIN + index * (ROW_HEIGHT + ROW_SPACING);
    row.setLayoutX(0);
    row.setLayoutY(y);
    rows.add(row);
    paneTableTask.getChildren().add(row);
    return row;
  }

  /**
   * Add multiple tasks as rows (appends to existing rows).
   *
   * @param tasks list of Task to add
   */
  public void addRows(List<Task> tasks) {
    if (tasks == null || tasks.isEmpty()) return;
    for (Task t : tasks) {
      addRow(t);
    }
  }

  /**
   * Replace current rows with the provided tasks.
   *
   * @param tasks list of Task to set
   */
  public void setTasks(List<Task> tasks) {
    clearRows();
    addRows(tasks);
  }

  /** Add an already created row control. */
  public void addRow(TaskerRowPanelController row) {
    if (row == null) return;
    attachRowListener(row);
    int index = rows.size();
    double y = TOP_MARGIN + index * (ROW_HEIGHT + ROW_SPACING);
    row.setLayoutX(0);
    row.setLayoutY(y);
    rows.add(row);
    paneTableTask.getChildren().add(row);
  }

  private void attachRowListener(TaskerRowPanelController row) {
    row.setRowActionListener(
        new TaskerRowPanelController.RowActionListener() {
          @Override
          public void onDeleteClicked(TaskerRowPanelController source) {
            if (tableActionListener != null) tableActionListener.onRowDeleteClicked(source);
          }

          @Override
          public void onEditClicked(TaskerRowPanelController source) {
            if (tableActionListener != null) tableActionListener.onRowEditClicked(source);
          }

          @Override
          public void onMoveUpClicked(TaskerRowPanelController source) {
            if (tableActionListener != null) tableActionListener.onRowMoveUpClicked(source);
          }

          @Override
          public void onMoveDownClicked(TaskerRowPanelController source) {
            if (tableActionListener != null) tableActionListener.onRowMoveDownClicked(source);
          }

          @Override
          public void onRowClicked(TaskerRowPanelController source) {
            if (tableActionListener != null)
              tableActionListener.onRowClicked(TaskerTablePanelController.this);
          }

          @Override
          public void onOpenClicked(TaskerRowPanelController source) {
            if (tableActionListener != null) tableActionListener.onRowOpenClicked(source);
          }
        });
  }

  /** Remove all rows from the table. */
  public void clearRows() {
    rows.clear();
    paneTableTask.getChildren().clear();
    reflowRows();
  }

  /** Recompute layoutY for all rows based on their index. */
  private void reflowRows() {
    for (int i = 0; i < rows.size(); i++) {
      TaskerRowPanelController r = rows.get(i);
      double y = TOP_MARGIN + i * (ROW_HEIGHT + ROW_SPACING);
      r.setLayoutY(y);
    }
  }

  private boolean isImageMoveLeftClicked(MouseEvent event) {
    return event.getSource() == imgMoveLeft;
  }

  private boolean isImageMoveRightClicked(MouseEvent event) {
    return event.getSource() == imgMoveRight;
  }

  @FXML
  void onMouseClicked(MouseEvent event) {
    if (isImageMoveLeftClicked(event)) {
      if (tableActionListener != null) tableActionListener.onMoveLeftClicked(this);
    } else if (isImageMoveRightClicked(event)) {
      if (tableActionListener != null) tableActionListener.onMoveRightClicked(this);
    } else if (event.getSource() == paneTableTask) {
      if (tableActionListener != null) tableActionListener.onRowClicked(this);
    }
  }
}
