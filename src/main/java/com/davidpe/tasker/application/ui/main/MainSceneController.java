package com.davidpe.tasker.application.ui.main;

import com.davidpe.tasker.application.service.task.TaskService;
import com.davidpe.tasker.application.task.DeleteTaskCommand;
import com.davidpe.tasker.application.task.DeleteTaskUseCase;
import com.davidpe.tasker.application.task.SetDoneTaskCommand;
import com.davidpe.tasker.application.task.SetDoneTaskUseCase;
import com.davidpe.tasker.application.task.TaskSequenceDirection;
import com.davidpe.tasker.application.task.UpdateTaskSequenceCommand;
import com.davidpe.tasker.application.task.UpdateTaskSequenceUseCase;
import com.davidpe.tasker.application.ui.common.UiScreen;
import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.application.ui.common.UiScreenFactory;
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.application.ui.controls.MessagePanelController;
import com.davidpe.tasker.application.ui.controls.TaskerRowPanelController;
import com.davidpe.tasker.application.ui.controls.TaskerTablePanelController;
import com.davidpe.tasker.application.ui.events.WindowEditTaskOpenedEvent;
import com.davidpe.tasker.application.ui.events.WindowMenuTaskOpenedEvent;
import com.davidpe.tasker.application.ui.events.WindowMenuTaskSelectedEvent;
import com.davidpe.tasker.application.ui.events.WindowNewTaskOpenedEvent;
import com.davidpe.tasker.application.ui.settings.SettingsSceneData;
import com.davidpe.tasker.domain.task.Tag;
import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskCreatedEvent;
import com.davidpe.tasker.domain.task.TaskDeletedEvent;
import com.davidpe.tasker.domain.task.TaskDoneUpdatedEvent;
import com.davidpe.tasker.domain.task.TaskSequenceUpdatedEvent;
import com.davidpe.tasker.domain.task.TaskUpdatedEvent;
import java.awt.Point;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MainSceneController extends UiScreenController {

  private double xOffset = 0;
  private double yOffset = 0;

  @FXML private Button btClose;

  @FXML private Button btSettings;

  @FXML private ImageView imgClose;

  @FXML private ImageView imgSettings;

  @FXML private Label lbUserInitials;

  @FXML private Text lblHello;

  @FXML private Pane mainPane;

  @FXML private StackPane pnlMenu;

  @FXML private Pane pnlMessage11;

  @FXML private HBox taskBar;

  @FXML private StackPane taskOption_analysis;

  @FXML private StackPane taskOption_games;

  @FXML private StackPane taskOption_settings;

  @FXML private StackPane taskOption_stats;

  @FXML private Label lblNewOp;

  @FXML private MessagePanelController pnlMessage;

  @FXML private TaskerTablePanelController pnlTaskerTable;

  @FXML private Button btFilter;

  @FXML private ImageView imgFilter;

  @FXML private Button btMenu;

  @FXML private ImageView imgMenu;

  @FXML private Text lbTitle;

  @FXML private Button btnNewProject;

  @FXML private Button btnNewTask;

  @FXML private Button btnShowStats;

  @FXML private Button btnSearch;

  @FXML private ImageView imgNewProject;

  @FXML private ImageView imgNewTask;

  @FXML private ImageView imgShowStats;

  @FXML private ImageView imgSearch;

  // Images for filter button (on / off)
  private Image imgFilterImageOn;
  private Image imgFilterImageOff;
  private boolean filterOn = true;

  // Reusable notification panel (separate from pnlMessage which is used for delete confirmation)
  private MessagePanelController pnlNotification;

  private final UiScreenFactory screenFactory;

  private final ApplicationEventPublisher eventPublisher;
  private final TaskService taskService;
  private final DeleteTaskUseCase deleteTaskUseCase;
  private final SetDoneTaskUseCase setDoneTaskUseCase;
  private final UpdateTaskSequenceUseCase updateTaskSequenceUseCase;

  private Long pendingTaskId;
  private PendingTaskAction pendingTaskAction;
  private TaskerRowPanelController hoveredRow;
  private TaskerRowPanelController selectedRow;

  private enum PendingTaskAction {
    DELETE,
    CLOSE
  }

  @Lazy
  public MainSceneController(
      UiScreenFactory screenFactory,
      ApplicationEventPublisher eventPublisher,
      TaskService taskService,
      DeleteTaskUseCase deleteTaskUseCase,
      SetDoneTaskUseCase setDoneTaskUseCase,
      UpdateTaskSequenceUseCase updateTaskSequenceUseCase) {

    this.screenFactory = screenFactory;
    this.eventPublisher = eventPublisher;
    this.taskService = taskService;
    this.deleteTaskUseCase = deleteTaskUseCase;
    this.setDoneTaskUseCase = setDoneTaskUseCase;
    this.updateTaskSequenceUseCase = updateTaskSequenceUseCase;
  }

  @FXML
  void buttonAction(ActionEvent event) {

    if (isButtonNewTaskClicked(event)) {

      eventPublisher.publishEvent(new WindowNewTaskOpenedEvent());
    }

    if (isButtonCloseClicked(event)) {

      hideStage();

      // TODO Send event to close;
      return;
    }

    if (isButtonSettingsClicked(event)) {

      UiScreen settings = screenFactory.create(UiScreenId.SETTINGS);
      settings.reset();
      settings.setData(new SettingsSceneData(Boolean.TRUE));
      settings.show();
      return;
    }

    if (isButtonShowStatsClicked(event)) {

      UiScreen stats = screenFactory.create(UiScreenId.STATS);
      stats.reset();
      stats.show();
      return;
    }

    if (isButtonFilterClicked(event)) {
      // Toggle filter icon between on/off
      try {
        filterOn = !filterOn;
        if (imgFilter != null) {
          if (filterOn && imgFilterImageOn != null) {
            imgFilter.setImage(imgFilterImageOn);
          } else if (!filterOn && imgFilterImageOff != null) {
            imgFilter.setImage(imgFilterImageOff);
          }
        }
      } catch (Exception ex) {
        // ignore
      }

      loadTasks();
      return;
    }
  }

  @FXML
  void handleButtonClick(MouseEvent event) {}

  private boolean isButtonNewProjectClicked(ActionEvent event) {

    return event.getSource() == btnNewProject || event.getSource() == imgNewProject;
  }

  private boolean isButtonNewTaskClicked(ActionEvent event) {

    return event.getSource() == btnNewTask;
  }

  private boolean isButtonShowStatsClicked(ActionEvent event) {

    return event.getSource() == btnShowStats;
  }

  private boolean isButtonSettingsClicked(ActionEvent event) {

    return event.getSource() == btSettings || event.getSource() == imgSettings;
  }

  private boolean isButtonNewOpClicked(MouseEvent event) {

    return event.getSource() == lblNewOp;
  }

  private boolean isButtonCloseClicked(ActionEvent event) {

    return event.getSource() == btClose || event.getSource() == imgClose;
  }

  private boolean isButtonFilterClicked(ActionEvent event) {

    return event.getSource() == btFilter || event.getSource() == imgFilter;
  }

  private boolean isButtonSearchClicked(ActionEvent event) {

    return event.getSource() == btnSearch || event.getSource() == imgSearch;
  }

  private void showDeletePanel(Pane panel) {

    panel.setVisible(!panel.isVisible());
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    moveMainWindowsSetUp();

    // Mostrar la fecha de hoy en formato largo en lblHello
    DateTimeFormatter longDateFmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    lblHello.setText(LocalDate.now().format(longDateFmt));

    // Load filter icons (fallback quietly if missing)
    try {
      var onUrl = getClass().getResource("/com/davidpe/tasker/ui/images/filter_alt_18dp_white.png");
      var offUrl =
          getClass().getResource("/com/davidpe/tasker/ui/images/filter_alt_off_18dp_white.png");
      if (onUrl != null) imgFilterImageOn = new Image(onUrl.toExternalForm());
      if (offUrl != null) imgFilterImageOff = new Image(offUrl.toExternalForm());
      if (imgFilter != null && imgFilterImageOn != null) {
        imgFilter.setImage(imgFilterImageOn);
        filterOn = true;
      }
    } catch (Exception e) {
      // ignore image loading errors
    }

    pnlMessage.setMessagePanelActionListener(
        new MessagePanelController.MessagePanelActionListener() {
          @Override
          public void onOkButtonClicked() {

            if (pendingTaskId != null && pendingTaskAction != null) {
              if (pendingTaskAction == PendingTaskAction.DELETE) {
                deleteTaskUseCase.deleteTask(new DeleteTaskCommand(pendingTaskId));
              } else if (pendingTaskAction == PendingTaskAction.CLOSE) {
                setDoneTaskUseCase.toggleDone(new SetDoneTaskCommand(pendingTaskId));
              }
            }
            pendingTaskId = null;
            pendingTaskAction = null;
            pnlMessage.setVisible(false);
          }

          @Override
          public void onCancelButtonClicked() {

            pendingTaskId = null;
            pendingTaskAction = null;
            pnlMessage.setVisible(false);
          }
        });

    pnlTaskerTable.setTableActionListener(
        new TaskerTablePanelController.TableActionListener() {
          @Override
          public void onMoveLeftClicked(TaskerTablePanelController source) {}

          @Override
          public void onMoveRightClicked(TaskerTablePanelController source) {}

          @Override
          public void onColumnToggleClicked(TaskerTablePanelController source) {
            Long selectedTaskId = selectedRow != null ? selectedRow.getTaskId() : null;
            loadTasks(selectedTaskId);
          }

          @Override
          public void onRowClicked(TaskerTablePanelController source) {}

          @Override
          public void onRowHovered(TaskerRowPanelController row) {
            if (row == null) return;
            hoveredRow = row;
          }

          @Override
          public void onRowExited(TaskerRowPanelController row) {
            if (row == null) return;
            if (hoveredRow == row) {
              hoveredRow = null;
            }
          }

          @Override
          public void onRowDoubleClicked(TaskerRowPanelController row) {
            if (row == null || row.getTaskId() == null || !row.isSelected()) return;
            eventPublisher.publishEvent(new WindowEditTaskOpenedEvent(row.getTaskId()));
          }

          @Override
          public void onRowDeleteClicked(TaskerRowPanelController row) {
            if (row == null || row.getTaskId() == null) return;
            pendingTaskId = row.getTaskId();
            pendingTaskAction = PendingTaskAction.DELETE;
            pnlMessage.setMessage("Do you really want to delete this task?");
            showDeletePanel(pnlMessage);
          }

          @Override
          public void onRowEditClicked(TaskerRowPanelController row) {
            if (row == null || row.getTaskId() == null) return;
            eventPublisher.publishEvent(new WindowEditTaskOpenedEvent(row.getTaskId()));
          }

          @Override
          public void onRowMoveUpClicked(TaskerRowPanelController row) {
            if (row == null || row.getTaskId() == null) return;
            updateTaskSequenceUseCase.updateSequence(
                new UpdateTaskSequenceCommand(row.getTaskId(), TaskSequenceDirection.UP));
          }

          @Override
          public void onRowMoveDownClicked(TaskerRowPanelController row) {
            if (row == null || row.getTaskId() == null) return;
            updateTaskSequenceUseCase.updateSequence(
                new UpdateTaskSequenceCommand(row.getTaskId(), TaskSequenceDirection.DOWN));
          }

          @Override
          public void onRowOpenClicked(TaskerRowPanelController row) {
            if (row == null || row.getTaskId() == null || row.isDone()) return;
            pendingTaskId = row.getTaskId();
            pendingTaskAction = PendingTaskAction.CLOSE;
            pnlMessage.setMessage("Do you really want to close this task?");
            showDeletePanel(pnlMessage);
          }

          @Override
          public void onRowContextMenuRequested(
              TaskerRowPanelController row, double screenX, double screenY) {
            if (row == null || row.getTaskId() == null) return;
            selectRow(row);
            eventPublisher.publishEvent(
                new WindowMenuTaskOpenedEvent(
                    row.getTaskId(), new Point((int) screenX, (int) screenY)));
          }

          @Override
          public void onRowSelected(TaskerRowPanelController row) {
            if (row == null) return;
            selectRow(row);
          }
        });

    pnlTaskerTable.addEventFilter(
        MouseEvent.MOUSE_CLICKED,
        event -> {
          if (event.getButton() != MouseButton.SECONDARY || event.isConsumed()) return;
          if (selectedRow == null || selectedRow.getTaskId() == null) return;
          eventPublisher.publishEvent(
              new WindowMenuTaskOpenedEvent(
                  selectedRow.getTaskId(),
                  new Point((int) event.getScreenX(), (int) event.getScreenY())));
          event.consume();
        });

    // load tasks initially so the window shows data on first presentation
    Platform.runLater(this::loadTasks);

    // Play a type-writer effect on the title and the hello label when the screen starts
    try {
      if (lbTitle != null) {
        // Use a short interval for a snappy effect
        com.davidpe.tasker.application.ui.effects.TypeWriterEffect.playTypeWriterEffect(
            " | tasker", lbTitle, 0.05);
      }

      if (lblHello != null) {
        String helloText = lblHello.getText() != null ? lblHello.getText() : "";
        com.davidpe.tasker.application.ui.effects.TypeWriterEffect.playTypeWriterEffect(
            helloText, lblHello, 0.08);
      }
    } catch (Throwable t) {
      // don't break initialization if the effect fails
    }
  }

  @Override
  public void resetData() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'resetData'");
  }

  @EventListener
  public void onMenuTaskSelected(WindowMenuTaskSelectedEvent event) {
    if (event == null || event.getTaskId() == null || event.getAction() == null) return;
    Platform.runLater(
        () -> {
          switch (event.getAction()) {
            case EDIT ->
                eventPublisher.publishEvent(new WindowEditTaskOpenedEvent(event.getTaskId()));
            case DELETE -> {
              pendingTaskId = event.getTaskId();
              pendingTaskAction = PendingTaskAction.DELETE;
              pnlMessage.setMessage("Do you really want to delete this task?");
              showDeletePanel(pnlMessage);
            }
            case PRIORITY_UP ->
                updateTaskSequenceUseCase.updateSequence(
                    new UpdateTaskSequenceCommand(event.getTaskId(), TaskSequenceDirection.UP));
            case PRIORITY_DOWN ->
                updateTaskSequenceUseCase.updateSequence(
                    new UpdateTaskSequenceCommand(event.getTaskId(), TaskSequenceDirection.DOWN));
            case DONE -> {
              pendingTaskId = event.getTaskId();
              pendingTaskAction = PendingTaskAction.CLOSE;
              pnlMessage.setMessage("Do you really want to close this task?");
              showDeletePanel(pnlMessage);
            }
            default -> {}
          }
        });
  }

  private void selectRow(TaskerRowPanelController row) {
    if (row == null) return;
    if (selectedRow != null && selectedRow != row) {
      selectedRow.setSelected(false);
    }
    selectedRow = row;
    selectedRow.setSelected(true);
  }

  private void moveMainWindowsSetUp() {

    mainPane.setOnMousePressed(
        event -> {
          xOffset = event.getSceneX();
          yOffset = event.getSceneY();
        });

    mainPane.setOnMouseDragged(
        event -> {
          Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

          stage.setX(event.getScreenX() - xOffset);
          stage.setY(event.getScreenY() - yOffset);
        });
  }

  @EventListener
  public void onTaskCreated(TaskCreatedEvent event) {
    Platform.runLater(
        () -> {
          loadTasks();
          showTaskNotification(event.entity(), "Se ha creado la tarea #");
        });
  }

  @EventListener
  public void onTaskEdited(TaskUpdatedEvent event) {
    Platform.runLater(
        () -> {
          loadTasks();
          showTaskNotification(event.entity(), "Se ha editado la tarea #");
        });
  }

  @EventListener
  public void onTaskDeleted(TaskDeletedEvent event) {
    Platform.runLater(
        () -> {
          loadTasks();
          showTaskNotification(event.entity(), "Se ha eliminado la tarea #");
        });
  }

  @EventListener
  public void onTaskDoneUpdated(TaskDoneUpdatedEvent event) {
    Platform.runLater(
        () -> {
          loadTasks();
          String prefix =
              Boolean.TRUE.equals(event.entity().getDone())
                  ? "Se ha marcado como done la tarea #"
                  : "Se ha marcado como pendiente la tarea #";
          showTaskNotification(event.entity(), prefix);
        });
  }

  @EventListener
  public void onTaskSequenceUpdated(TaskSequenceUpdatedEvent event) {
    Platform.runLater(
        () -> {
          loadTasks();
          if (event.entity() != null) {
            System.out.println("Task sequence updated for task #" + event.entity().getId());
          }
        });
  }

  private void showTaskNotification(Task task, String prefix) {

    if (task == null) return;

    ensureNotificationPanel();

    String title = task.getTitle() != null ? " : " + task.getTitle() : "";
    String message = prefix + task.getId() + title;

    pnlNotification.setMessage(message);
    pnlNotification.setVisible(true);
  }

  private void ensureNotificationPanel() {

    if (pnlNotification != null) return;

    pnlNotification = new MessagePanelController();
    pnlNotification.setVisible(false);

    // Hide the notification when user clicks any button
    pnlNotification.setMessagePanelActionListener(
        new MessagePanelController.MessagePanelActionListener() {
          @Override
          public void onOkButtonClicked() {
            pnlNotification.setVisible(false);
          }

          @Override
          public void onCancelButtonClicked() {
            pnlNotification.setVisible(false);
          }
        });

    mainPane.getChildren().add(pnlNotification);

    pnlNotification.setLayoutX(20);
    pnlNotification
        .layoutYProperty()
        .bind(mainPane.heightProperty().subtract(pnlNotification.heightProperty()).subtract(20));
  }

  private void loadTasks() {
    loadTasks(null);
  }

  private void loadTasks(Long preserveTaskId) {

    // Populate the Tasker table panel with tasks.
    List<Task> tasks = filterOn ? taskService.getTasksNotDone() : taskService.getTasks();
    List<Task> orderedTasks = new ArrayList<>(tasks);
    orderedTasks.sort(
        Comparator.comparing(Task::getSequence, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Task::getCreatedAt, Comparator.reverseOrder()));
    populateTaskerPanel(orderedTasks, preserveTaskId);
  }

  private void populateTaskerPanel(List<Task> orderedTasks) {
    populateTaskerPanel(orderedTasks, null);
  }

  private void populateTaskerPanel(List<Task> orderedTasks, Long preserveTaskId) {
    if (pnlTaskerTable == null) return;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    pnlTaskerTable.setTasks(orderedTasks);
    if (orderedTasks == null || orderedTasks.isEmpty()) {
      if (selectedRow != null) {
        selectedRow.setSelected(false);
      }
      selectedRow = null;
      return;
    }

    List<TaskerRowPanelController> rows = pnlTaskerTable.getRows();
    for (int i = 0; i < orderedTasks.size() && i < rows.size(); i++) {
      Task task = orderedTasks.get(i);
      TaskerRowPanelController row = rows.get(i);
      row.setTaskId(task.getId());
      row.setName(task.getTitle() != null ? task.getTitle() : "");
      String priorityText =
          task.getPriorityId() != null
              ? taskService
                  .getPriorityById(task.getPriorityId())
                  .map(priority -> priority.getDescription())
                  .orElse("")
              : "";
      row.setPriority(priorityText);
      if (task.getStartAt() != null) {
        LocalDateTime ldt = LocalDateTime.ofInstant(task.getStartAt(), ZoneId.systemDefault());
        row.setDate(dtf.format(ldt));
      } else {
        row.setDate("");
      }
      row.setDone(Boolean.TRUE.equals(task.getDone()));
      String tagText =
          task.getTagId() != null
              ? taskService.getTagById(task.getTagId()).map(Tag::getName).orElse("")
              : "";
      row.setTags(tagText, "");
    }

    if (preserveTaskId != null) {
      TaskerRowPanelController rowToSelect = findRowByTaskId(preserveTaskId);
      if (rowToSelect != null) {
        selectRow(rowToSelect);
        pnlTaskerTable.ensureRowVisibleByTaskId(preserveTaskId);
      } else if (selectedRow != null) {
        selectedRow.setSelected(false);
        selectedRow = null;
      }
    }
  }

  private TaskerRowPanelController findRowByTaskId(Long taskId) {
    if (taskId == null || pnlTaskerTable == null) return null;
    for (TaskerRowPanelController row : pnlTaskerTable.getRows()) {
      if (row != null && taskId.equals(row.getTaskId())) {
        return row;
      }
    }
    return null;
  }
}
