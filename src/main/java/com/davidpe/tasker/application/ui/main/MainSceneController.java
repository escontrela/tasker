package com.davidpe.tasker.application.ui.main;

import static javafx.collections.FXCollections.observableArrayList;

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
import com.davidpe.tasker.application.ui.events.WindowEditTaskOpenedEvent;
import com.davidpe.tasker.application.ui.events.WindowNewTaskOpenedEvent;
import com.davidpe.tasker.application.ui.settings.SettingsSceneData;
import com.davidpe.tasker.domain.task.Tag;
import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskCreatedEvent;
import com.davidpe.tasker.domain.task.TaskDeletedEvent;
import com.davidpe.tasker.domain.task.TaskDoneUpdatedEvent;
import com.davidpe.tasker.domain.task.TaskSequenceUpdatedEvent;
import com.davidpe.tasker.domain.task.TaskUpdatedEvent;
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
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
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

  @FXML private Button btLeft;

  @FXML private Button btRight;

  @FXML private Button btSettings;

  @FXML private ImageView imgClose;

  @FXML private ImageView imgMinimize12222;

  @FXML private ImageView imgMinimize12223;

  @FXML private ImageView imgSettings;

  @FXML private Label lbUserInitials;

  @FXML private Text lblHello;

  @FXML private Text lblPractice;

  @FXML private Pane mainPane;

  @FXML private StackPane pnlMenu;

  @FXML private Pane pnlMessage11;

  @FXML private HBox taskBar;

  @FXML private StackPane taskOption_analysis;

  @FXML private StackPane taskOption_games;

  @FXML private StackPane taskOption_settings;

  @FXML private StackPane taskOption_stats;

  @FXML private Label lblNewOp;

  @FXML private TableView<Task> tableTasks;

  @FXML private TableColumn<Task, String> tcolFin;

  @FXML private TableColumn<Task, String> tcolInicio;

  @FXML private TableColumn<Task, String> tcolPriority;

  @FXML private TableColumn<Task, String> tcolTaskName;

  @FXML private TableColumn<Task, String> tcolTaskStatus;

  @FXML private TableColumn<Task, String> tcolTaskTags;

  @FXML private MessagePanelController pnlMessage;

  @FXML private Button btFilter;

  @FXML private ImageView imgFilter;

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
  void handleButtonClick(MouseEvent event) {

    if (isButtonNewOpClicked(event)) {

      eventPublisher.publishEvent(new WindowNewTaskOpenedEvent());
    }
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

  private void showDeletePanel(Pane panel) {

    panel.setVisible(!panel.isVisible());
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    moveMainWindowsSetUp();

    // Mostrar la fecha de hoy en formato largo en lblHello
    DateTimeFormatter longDateFmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    lblHello.setText(LocalDate.now().format(longDateFmt));
    lblPractice.setText("— (S: borrar tarea) (D: marcar done) (Q/A: mover prioridad)");

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

    // Configure table columns
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    tcolTaskName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitle()));

    tcolPriority.setCellValueFactory(
        cell -> {
          Long pid = cell.getValue().getPriorityId();
          String txt = "";
          if (pid != null) {
            // lookup priority
            txt =
                taskService
                    .getPriorityById(pid)
                    .map(priority -> priority.getDescription())
                    .orElse("");
          }
          return new SimpleStringProperty(txt);
        });

    tcolInicio.setCellValueFactory(
        cell -> {
          if (cell.getValue().getStartAt() == null) return new SimpleStringProperty("");
          LocalDateTime ldt =
              LocalDateTime.ofInstant(cell.getValue().getStartAt(), ZoneId.systemDefault());
          return new SimpleStringProperty(dtf.format(ldt));
        });

    tcolFin.setCellValueFactory(
        cell -> {
          if (cell.getValue().getEndAt() == null) return new SimpleStringProperty("");
          LocalDateTime ldt =
              LocalDateTime.ofInstant(cell.getValue().getEndAt(), ZoneId.systemDefault());
          return new SimpleStringProperty(dtf.format(ldt));
        });

    tcolTaskStatus.setCellValueFactory(
        cell -> {
          String status = Boolean.TRUE.equals(cell.getValue().getDone()) ? "Done" : "Open";
          return new SimpleStringProperty(status);
        });

    tcolTaskTags.setCellValueFactory(
        cell -> {
          Long tagId = cell.getValue().getTagId();
          String tagText = "";
          if (tagId != null) {
            tagText = taskService.getTagById(tagId).map(Tag::getName).orElse("");
          }
          return new SimpleStringProperty(tagText);
        });

    // Open edit dialog when a table row is double-clicked
    tableTasks.setRowFactory(
        tv -> {
          TableRow<Task> row = new TableRow<>();
          row.setOnMouseClicked(
              evt -> {
                if (!row.isEmpty() && evt.getClickCount() == 2) {
                  Task clickedTask = row.getItem();
                  eventPublisher.publishEvent(new WindowEditTaskOpenedEvent(clickedTask.getId()));
                }
              });
          return row;
        });

    // Delete selected task when the Delete (Supr) key is pressed
    tableTasks.setOnKeyPressed(
        evt -> {
          if (evt.getCode() == KeyCode.DELETE || evt.getCode() == KeyCode.S) {

            Task selected = tableTasks.getSelectionModel().getSelectedItem();

            if (selected != null) {
              try {

                showDeletePanel(pnlMessage);

              } catch (Exception ex) {

                System.err.println("Error deleting task: " + ex.getMessage());
              }
            }
          }
          if (evt.getCode() == KeyCode.D) {
            Task selected = tableTasks.getSelectionModel().getSelectedItem();
            if (selected != null) {
              try {
                setDoneTaskUseCase.toggleDone(new SetDoneTaskCommand(selected.getId()));
              } catch (Exception ex) {
                System.err.println("Error updating task done status: " + ex.getMessage());
              }
            }
          }
          if (evt.getCode() == KeyCode.Q || evt.getCode() == KeyCode.A) {
            Task selected = tableTasks.getSelectionModel().getSelectedItem();
            if (selected != null) {
              try {
                TaskSequenceDirection direction =
                    evt.getCode() == KeyCode.Q
                        ? TaskSequenceDirection.UP
                        : TaskSequenceDirection.DOWN;
                updateTaskSequenceUseCase.updateSequence(
                    new UpdateTaskSequenceCommand(selected.getId(), direction));
              } catch (Exception ex) {
                System.err.println("Error updating task sequence: " + ex.getMessage());
              }
            }
          }
        });

    pnlMessage.setMessage("Do you really want to delete this task?");
    pnlMessage.setMessagePanelActionListener(
        new MessagePanelController.MessagePanelActionListener() {
          @Override
          public void onOkButtonClicked() {

            Task selected = tableTasks.getSelectionModel().getSelectedItem();
            deleteTaskUseCase.deleteTask(new DeleteTaskCommand(selected.getId()));
            pnlMessage.setVisible(false);
          }

          @Override
          public void onCancelButtonClicked() {

            pnlMessage.setVisible(false);
          }
        });

    // load tasks initially so the window shows data on first presentation
    Platform.runLater(this::loadTasks);
  }

  @Override
  public void resetData() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'resetData'");
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

    // Populate the TableView with tasks instead of the old tasksContainer labels
    List<Task> tasks = filterOn ? taskService.getTasksNotDone() : taskService.getTasks();
    List<Task> orderedTasks = new ArrayList<>(tasks);
    orderedTasks.sort(
        Comparator.comparing(Task::getSequence, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Task::getCreatedAt, Comparator.reverseOrder()));
    tableTasks.setItems(observableArrayList(orderedTasks));
  }
}
