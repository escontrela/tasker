package com.davidpe.tasker.application.ui.menu;

import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.application.ui.events.WindowMenuTaskSelectedEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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

  @FXML private ImageView imgDone;

  @FXML private ImageView imgEdit;

  @FXML private ImageView imgPriorityDown;

  @FXML private ImageView imgPriorityUp;

  @FXML private Label lblDelete;

  @FXML private Label lblDone;

  @FXML private Label lblEdit;

  @FXML private Label lblPriorityDown;

  @FXML private Label lblPriorityUp;

  @FXML private Pane paneDelete;

  @FXML private Pane paneDone;

  @FXML private Pane paneEdit;

  @FXML private Pane paneMenuTask;

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
    } else if (src == paneDone || src == lblDone || src == imgDone) {
      action = WindowMenuTaskSelectedEvent.Action.DONE;
    }

    if (action != null) {
      eventPublisher.publishEvent(new WindowMenuTaskSelectedEvent(taskData.getTaskId(), action));
    }

    hideStage();
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
