package com.davidpe.tasker.application.ui.controls;

import com.davidpe.tasker.domain.task.Task;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;

/** A scrollable, adaptive two-column task grid. */
public class TaskerTablePanelController extends BorderPane {

  public interface TableActionListener {
    void onMoveLeftClicked(TaskerTablePanelController source);
    void onMoveRightClicked(TaskerTablePanelController source);
    void onColumnToggleClicked(TaskerTablePanelController source);
    void onRowClicked(TaskerTablePanelController source);
    void onRowHovered(TaskerRowPanelController row);
    void onRowExited(TaskerRowPanelController row);
    void onRowDoubleClicked(TaskerRowPanelController row);
    void onRowSelected(TaskerRowPanelController row);
    void onRowContextMenuRequested(TaskerRowPanelController row, double screenX, double screenY);
    void onRowDeleteClicked(TaskerRowPanelController row);
    void onRowEditClicked(TaskerRowPanelController row);
    void onRowMoveUpClicked(TaskerRowPanelController row);
    void onRowMoveDownClicked(TaskerRowPanelController row);
    void onRowOpenClicked(TaskerRowPanelController row);
  }

  private final List<TaskerRowPanelController> rows = new ArrayList<>();
  private final TilePane cards = new TilePane();
  private TableActionListener tableActionListener;

  public TaskerTablePanelController() {
    getStyleClass().add("task-grid-shell");
    cards.getStyleClass().add("task-grid");
    cards.setPrefColumns(2);
    cards.setPrefTileWidth(390);
    cards.setHgap(16);
    cards.setVgap(16);
    cards.setPadding(new Insets(4));
    ScrollPane scrollPane = new ScrollPane(cards);
    scrollPane.setFitToWidth(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.getStyleClass().add("task-grid-scroll");
    setCenter(scrollPane);
  }

  public void setTableActionListener(TableActionListener listener) { tableActionListener = listener; }
  public TaskerRowPanelController addRow(Task task) {
    TaskerRowPanelController row = new TaskerRowPanelController();
    row.setTask(task);
    addRow(row);
    return row;
  }
  public void addRows(List<Task> tasks) { if (tasks != null) tasks.forEach(this::addRow); }
  public void addRow(TaskerRowPanelController row) {
    if (row == null) return;
    attachRowListener(row);
    rows.add(row);
    cards.getChildren().add(row);
  }
  public void setTasks(List<Task> tasks) {
    clearRows();
    if (tasks != null) tasks.forEach(this::addRow);
  }
  public void clearRows() { rows.clear(); cards.getChildren().clear(); }
  public List<TaskerRowPanelController> getRows() { return new ArrayList<>(rows); }
  public int getColumnCount() { return 2; }
  public void setColumnCount(int ignored) { }
  public void toggleColumnCount() { if (tableActionListener != null) tableActionListener.onColumnToggleClicked(this); }
  public void ensureRowVisibleByTaskId(Long ignored) { }

  private void attachRowListener(TaskerRowPanelController row) {
    row.setRowActionListener(new TaskerRowPanelController.RowActionListener() {
      @Override public void onDeleteClicked(TaskerRowPanelController source) { if (tableActionListener != null) tableActionListener.onRowDeleteClicked(source); }
      @Override public void onEditClicked(TaskerRowPanelController source) { if (tableActionListener != null) tableActionListener.onRowEditClicked(source); }
      @Override public void onMoveUpClicked(TaskerRowPanelController source) { if (tableActionListener != null) tableActionListener.onRowMoveUpClicked(source); }
      @Override public void onMoveDownClicked(TaskerRowPanelController source) { if (tableActionListener != null) tableActionListener.onRowMoveDownClicked(source); }
      @Override public void onOpenClicked(TaskerRowPanelController source) { if (tableActionListener != null) tableActionListener.onRowOpenClicked(source); }
      @Override public void onRowClicked(TaskerRowPanelController source) { if (tableActionListener != null) tableActionListener.onRowSelected(source); }
      @Override public void onRowHovered(TaskerRowPanelController source) { if (tableActionListener != null) tableActionListener.onRowHovered(source); }
      @Override public void onRowExited(TaskerRowPanelController source) { if (tableActionListener != null) tableActionListener.onRowExited(source); }
      @Override public void onRowDoubleClicked(TaskerRowPanelController source) { if (tableActionListener != null) tableActionListener.onRowDoubleClicked(source); }
      @Override public void onRowContextMenuRequested(TaskerRowPanelController source, double x, double y) { if (tableActionListener != null) tableActionListener.onRowContextMenuRequested(source, x, y); }
    });
  }
}
