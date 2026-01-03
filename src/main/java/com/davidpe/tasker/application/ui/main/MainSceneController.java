package com.davidpe.tasker.application.ui.main;

import com.davidpe.tasker.application.task.TaskCreatedEvent;
import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.application.ui.events.WindowOpenedEvent;
import com.davidpe.tasker.domain.task.Priority;
import com.davidpe.tasker.domain.task.PriorityRepository;
import com.davidpe.tasker.domain.task.Tag;
import com.davidpe.tasker.domain.task.TagRepository;
import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskRepository;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MainSceneController extends UiScreenController {

    @FXML
    private Button btnNewTask;

    @FXML
    private VBox tasksContainer;

    @FXML
    private Label lblTitle;

    private final ApplicationEventPublisher eventPublisher;
    private final TaskRepository taskRepository;
    private final PriorityRepository priorityRepository;
    private final TagRepository tagRepository;

    @Lazy
    public MainSceneController(ApplicationEventPublisher eventPublisher,
                               TaskRepository taskRepository,
                               PriorityRepository priorityRepository,
                               TagRepository tagRepository) {
        this.eventPublisher = eventPublisher;
        this.taskRepository = taskRepository;
        this.priorityRepository = priorityRepository;
        this.tagRepository = tagRepository;
    }

    @FXML
    void buttonAction(ActionEvent event) {
        if (event.getSource() == btnNewTask) {
            eventPublisher.publishEvent(new WindowOpenedEvent(UiScreenId.NEW_TASK_DIALOG));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblTitle.setText("Tasker");
        loadTasks();
    }

    @Override
    public void resetData() {
        loadTasks();
    }

    @EventListener
    public void onTaskCreated(TaskCreatedEvent event) {
        Platform.runLater(this::loadTasks);
    }

    private void loadTasks() {
        tasksContainer.getChildren().clear();
        List<Task> tasks = taskRepository.findAll();
        Map<Long, Priority> priorities = priorityRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Priority::getId, p -> p));

        for (Task task : tasks) {
            Priority priority = priorities.get(task.getPriorityId());
            String priorityText = priority != null ? priority.getDescription() : "";
            Tag tag = task.getTagId() != null ? tagRepository.findById(task.getTagId()).orElse(null) : null;
            String tagText = tag != null ? tag.getName() : "";
            Label label = new Label(formatTaskLabel(task, priorityText, tagText));
            label.getStyleClass().add("task-label");
            tasksContainer.getChildren().add(label);
        }
    }

    private String formatTaskLabel(Task task, String priorityText, String tagText) {
        StringBuilder builder = new StringBuilder(task.getTitle());
        if (!priorityText.isBlank()) {
            builder.append(" • ").append(priorityText);
        }
        if (!tagText.isBlank()) {
            builder.append(" • ").append(tagText);
        }
        return builder.toString();
    }
}
