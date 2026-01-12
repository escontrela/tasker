package com.davidpe.tasker.application.ui.main;
 
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.davidpe.tasker.application.task.TaskCreatedEvent;
import com.davidpe.tasker.application.ui.common.UiScreen;
import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.application.ui.common.UiScreenFactory;
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.application.ui.events.WindowNewTaskOpenedEvent;
import com.davidpe.tasker.application.ui.events.WindowEditTaskOpenedEvent;
import com.davidpe.tasker.application.ui.settings.SettingsSceneData;
import com.davidpe.tasker.domain.task.Priority;
import com.davidpe.tasker.domain.task.PriorityRepository;
import com.davidpe.tasker.domain.task.Tag;
import com.davidpe.tasker.domain.task.TagRepository;
import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskRepository;

import javafx.scene.Node;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML; 
import javafx.scene.control.Button;
import javafx.scene.control.Label; 

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
 
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;

import javafx.beans.property.SimpleStringProperty;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;

@Component
public class MainSceneController extends UiScreenController {
    
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private Button btClose;

    @FXML
    private Button btLeft;

    @FXML
    private Button btRight;

    @FXML
    private Button btSettings;

    @FXML
    private ImageView imgClose;

    @FXML
    private ImageView imgMinimize12222;

    @FXML
    private ImageView imgMinimize12223;

    @FXML
    private ImageView imgSettings;

    @FXML
    private Label lbUserInitials;

    @FXML
    private Text lblChessboard;

    @FXML
    private Text lblPractice;

    @FXML
    private Pane mainPane;

    @FXML
    private StackPane pnlMenu;

    @FXML
    private Pane pnlMessage11;

    @FXML
    private HBox taskBar;

    @FXML
    private StackPane taskOption_analysis;

    @FXML
    private StackPane taskOption_games;

    @FXML
    private StackPane taskOption_settings;

    @FXML
    private StackPane taskOption_stats;

    @FXML
    private Label lblNewOp;

    @FXML
    private TableView<Task> tableTasks;

    @FXML
    private TableColumn<Task, String> tcolFin;

    @FXML
    private TableColumn<Task, String> tcolInicio;

    @FXML
    private TableColumn<Task, String> tcolPriority;

    @FXML
    private TableColumn<Task, String> tcolTaskName;

    @FXML
    private TableColumn<Task, String> tcolTaskStatus;

    @FXML
    private TableColumn<Task, String> tcolTaskTags;

    private final UiScreenFactory screenFactory;
 
    private final ApplicationEventPublisher eventPublisher;
    private final TaskRepository taskRepository;
    private final PriorityRepository priorityRepository;
    private final TagRepository tagRepository;

    @Lazy
    public MainSceneController(UiScreenFactory screenFactory,
                               ApplicationEventPublisher eventPublisher,
                               TaskRepository taskRepository,
                               PriorityRepository priorityRepository,
                               TagRepository tagRepository) {

        this.screenFactory = screenFactory;
        this.eventPublisher = eventPublisher;
        this.taskRepository = taskRepository;
        this.priorityRepository = priorityRepository;
        this.tagRepository = tagRepository;
    }

    @FXML
    void buttonAction(ActionEvent event) {

         if (isButtonCloseClicked(event)) {

            hideStage();
             
            //TODO Send event to close;
            return;
         }

        if (isButtonSettingsClicked(event)) {

            UiScreen settings = screenFactory.create(UiScreenId.SETTINGS);
            settings.reset();
            settings.setData(new SettingsSceneData(Boolean.TRUE));
            settings.show();
            return;
         }
    }

    @FXML
    void handleButtonClick(MouseEvent event) {

        if (isButtonNewOpClicked(event)){

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moveMainWindowsSetUp();

        // Configure table columns
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        tcolTaskName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitle()));

        tcolPriority.setCellValueFactory(cell -> {
            Long pid = cell.getValue().getPriorityId();
            String txt = "";
            if (pid != null) {
                // lookup priority
                Priority p = priorityRepository.findAll()
                        .stream()
                        .filter(pr -> pr.getId().equals(pid))
                        .findFirst()
                        .orElse(null);
                txt = p != null ? p.getDescription() : "";
            }
            return new SimpleStringProperty(txt);
        });

        tcolInicio.setCellValueFactory(cell -> {
            if (cell.getValue().getStartAt() == null) return new SimpleStringProperty("");
            LocalDateTime ldt = LocalDateTime.ofInstant(cell.getValue().getStartAt(), ZoneId.systemDefault());
            return new SimpleStringProperty(dtf.format(ldt));
        });

        tcolFin.setCellValueFactory(cell -> {
            if (cell.getValue().getEndAt() == null) return new SimpleStringProperty("");
            LocalDateTime ldt = LocalDateTime.ofInstant(cell.getValue().getEndAt(), ZoneId.systemDefault());
            return new SimpleStringProperty(dtf.format(ldt));
        });

        tcolTaskStatus.setCellValueFactory(cell -> {
            Instant now = Instant.now();
            String status = "";
            if (cell.getValue().getEndAt() != null && cell.getValue().getEndAt().isBefore(now)) {
                status = "Done";
            } else {
                status = "Open";
            }
            return new SimpleStringProperty(status);
        });

        tcolTaskTags.setCellValueFactory(cell -> {
            Long tagId = cell.getValue().getTagId();
            String tagText = "";
            if (tagId != null) {
                tagText = tagRepository.findById(tagId).map(Tag::getName).orElse("");
            }
            return new SimpleStringProperty(tagText);
        });

        // Open edit dialog when a table row is clicked
        tableTasks.setRowFactory(tv -> {
            TableRow<Task> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (!row.isEmpty()) {
                    Task clickedTask = row.getItem();
                    eventPublisher.publishEvent(new WindowEditTaskOpenedEvent(clickedTask.getId()));
                }
            });
            return row;
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

        Platform.runLater(this::loadTasks);
    }

            private void loadTasks() {

                // Populate the TableView with tasks instead of the old tasksContainer labels
                List<Task> tasks = taskRepository.findAll();
                tableTasks.setItems(javafx.collections.FXCollections.observableArrayList(tasks));
        }

    

}