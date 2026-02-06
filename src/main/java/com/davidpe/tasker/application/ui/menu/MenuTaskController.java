package com.davidpe.tasker.application.ui.menu;

import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.application.ui.events.WindowMenuTaskSelectedEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MenuTaskController extends UiScreenController implements MenuTaskView {

  private final ApplicationEventPublisher eventPublisher;

  private MenuTaskData taskData;

  @FXML private ImageView imgDelete;

  @FXML private ImageView imgBacklog;

  @FXML private ImageView imgPlanned;

  @FXML private ImageView imgInProgress;

  @FXML private ImageView imgDone;

  @FXML private ImageView imgEdit;

  @FXML private ImageView imgPriorityDown;

  @FXML private ImageView imgPriorityUp;

  @FXML private Label lblDelete;

  @FXML private Label lblBacklog;

  @FXML private Label lblPlanned;

  @FXML private Label lblInProgress;

  @FXML private Label lblDone;

  @FXML private Label lblEdit;

  @FXML private Label lblPriorityDown;

  @FXML private Label lblPriorityUp;

  @FXML private Pane paneDelete;

  @FXML private Pane paneBacklog;

  @FXML private Pane panePlanned;

  @FXML private Pane paneInProgress;

  @FXML private Pane paneDone;

  @FXML private Pane paneEdit;

  @FXML private Pane paneMenuTask;

  private final EventHandler<MouseEvent> sceneClickHandler =
      event -> {
        // If no task data, already hidden by other logic; still allow close when clicking outside
        Node target =
            event.getPickResult() != null ? event.getPickResult().getIntersectedNode() : null;
        if (target == null || !isDescendantOf(target, paneMenuTask)) {
          hideStage();
        }
      };

  @FXML private Pane panePriorityDown;

  @FXML private Pane panePriorityUp;

  @Lazy
  public MenuTaskController(ApplicationEventPublisher eventPublisher) {

    this.eventPublisher = eventPublisher;
  }

  @FXML
  void onMouseClicked(MouseEvent event) {

    if (taskData == null) {
      hideStage();
      return;
    }

    Object src = event.getSource();

    WindowMenuTaskSelectedEvent.Action action = null;

    if (src == paneEdit || src == lblEdit || src == imgEdit) {
      action = WindowMenuTaskSelectedEvent.Action.EDIT;
    } else if (src == paneDelete || src == lblDelete || src == imgDelete) {
      action = WindowMenuTaskSelectedEvent.Action.DELETE;
    } else if (src == panePriorityUp || src == lblPriorityUp || src == imgPriorityUp) {
      action = WindowMenuTaskSelectedEvent.Action.PRIORITY_UP;
    } else if (src == panePriorityDown || src == lblPriorityDown || src == imgPriorityDown) {
      action = WindowMenuTaskSelectedEvent.Action.PRIORITY_DOWN;
    } else if (src == paneBacklog || src == lblBacklog || src == imgBacklog) {
      action = WindowMenuTaskSelectedEvent.Action.SET_BACKLOG;
    } else if (src == panePlanned || src == lblPlanned || src == imgPlanned) {
      action = WindowMenuTaskSelectedEvent.Action.SET_PLANNED;
    } else if (src == paneInProgress || src == lblInProgress || src == imgInProgress) {
      action = WindowMenuTaskSelectedEvent.Action.SET_IN_PROGRESS;
    } else if (src == paneDone || src == lblDone || src == imgDone) {
      action = WindowMenuTaskSelectedEvent.Action.SET_DONE;
    }

    if (action != null) {
      eventPublisher.publishEvent(new WindowMenuTaskSelectedEvent(taskData.getTaskId(), action));
    }

    hideStage();
  }

  @Override
  public void setRootStage(Stage stage) {
    // Attach/detach handler when scene changes
    Stage previous = getRootStage();
    super.setRootStage(stage);

    if (previous != null) {
      Scene oldScene = previous.getScene();
      if (oldScene != null) {
        oldScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, sceneClickHandler);
      }
    }

    if (stage != null) {
      stage
          .sceneProperty()
          .addListener(
              (obs, oldScene, newScene) -> {
                if (oldScene != null) {
                  oldScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, sceneClickHandler);
                }
                if (newScene != null) {
                  newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneClickHandler);
                }
              });

      Scene s = stage.getScene();
      if (s != null) {
        s.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneClickHandler);
      }
    }
  }

  private boolean isDescendantOf(Node node, Node ancestor) {
    Node current = node;
    while (current != null) {
      if (current == ancestor) {
        return true;
      }
      current = current.getParent();
    }
    return false;
  }

  @Override
  public void setData(MenuTaskData data) {
    this.taskData = data;
  }

  @Override
  public MenuTaskData getData() {
    return taskData;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Controller initialized as a simple popup menu. Nothing else required here.
  }

  @Override
  public void resetData() {
    this.taskData = null;
  }
}
