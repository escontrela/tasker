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
  private int currentPage = 0;

  // layout constants
  private static final double ROW_HEIGHT = 37.0;
  private static final double ROW_SPACING = 5.0;
  private static final double TOP_MARGIN = 10.0;
  private static final double ROW_OFFSET_X = -10.0;
  private static final int MAX_ROWS = 12;

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
    rows.add(row);
    renderPage();
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
    if (tasks == null || tasks.isEmpty()) {
      renderPage();
      return;
    }
    for (Task t : tasks) {
      TaskerRowPanelController row = new TaskerRowPanelController();
      row.setTask(t);
      attachRowListener(row);
      rows.add(row);
    }
    currentPage = 0;
    renderPage();
  }

  /** Add an already created row control. */
  public void addRow(TaskerRowPanelController row) {
    if (row == null) return;
    attachRowListener(row);
    rows.add(row);
    renderPage();
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
    currentPage = 0;
    updateNavigationVisibility();
  }

  public List<TaskerRowPanelController> getRows() {
    return new ArrayList<>(rows);
  }

  private void renderPage() {
    paneTableTask.getChildren().clear();
    int start = currentPage * MAX_ROWS;
    int end = Math.min(start + MAX_ROWS, rows.size());
    int visibleIndex = 0;
    for (int i = start; i < end; i++) {
      TaskerRowPanelController r = rows.get(i);
      double y = TOP_MARGIN + visibleIndex * (ROW_HEIGHT + ROW_SPACING);
      r.setLayoutX(ROW_OFFSET_X);
      r.setLayoutY(y);
      paneTableTask.getChildren().add(r);
      visibleIndex++;
    }
    updateNavigationVisibility();
  }

  private void updateNavigationVisibility() {
    int totalPages = (int) Math.ceil(rows.size() / (double) MAX_ROWS);
    boolean hasMultiplePages = totalPages > 1;
    imgMoveLeft.setVisible(hasMultiplePages && currentPage > 0);
    imgMoveRight.setVisible(hasMultiplePages && currentPage < totalPages - 1);
  }

  private void movePage(int delta) {
    int totalPages = (int) Math.ceil(rows.size() / (double) MAX_ROWS);
    if (totalPages <= 1) return;
    int nextPage = Math.max(0, Math.min(currentPage + delta, totalPages - 1));
    if (nextPage != currentPage) {
      currentPage = nextPage;
      renderPage();
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
      movePage(-1);
      if (tableActionListener != null) tableActionListener.onMoveLeftClicked(this);
    } else if (isImageMoveRightClicked(event)) {
      movePage(1);
      if (tableActionListener != null) tableActionListener.onMoveRightClicked(this);
    } else if (event.getSource() == paneTableTask) {
      if (tableActionListener != null) tableActionListener.onRowClicked(this);
    }
  }
}
