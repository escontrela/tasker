package com.davidpe.tasker.application.ui.stats;

import com.davidpe.tasker.application.ui.common.UiControllerDataAware;
import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.application.ui.common.UiScreenFactory;
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.application.ui.controls.Chart2DController;
import com.davidpe.tasker.application.ui.events.WindowClosedEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class StatsSceneController extends UiScreenController
    implements UiControllerDataAware<StatsSceneData> {

  @FXML private Button btClose;

  @FXML private Button btLeft;

  @FXML private Button btRight;

  @FXML private Button btSettings;

  @FXML private ImageView imgClose;

  @FXML private ImageView imgMinimize12222;

  @FXML private ImageView imgMinimize12223;

  @FXML private Text lblChessboard;

  @FXML private Text lblPractice;

  @FXML private Pane mainPane;
  @FXML private Button btnFilter;

  @FXML private ComboBox<?> cbxGroupBy;

  @FXML private ComboBox<?> cbxProject;

  @FXML private DatePicker dpEndDate;

  @FXML private DatePicker dpStartDate;

  @FXML private Chart2DController grhStats;

  private final UiScreenFactory screenFactory;

  private ApplicationEventPublisher eventPublisher;

  @Lazy
  public StatsSceneController(
      UiScreenFactory screenFactory, ApplicationEventPublisher eventPublisher) {

    this.eventPublisher = eventPublisher;
    this.screenFactory = screenFactory;
  }

  @FXML
  void buttonAction(ActionEvent event) {

    if (isButtonCloseClicked(event)) {

      lblPractice.setText("Bye.");
      eventPublisher.publishEvent(new WindowClosedEvent(UiScreenId.STATS));

      return;
    }

    if (isButtonLeftClicked(event)) {

      return;
    }
  }

  @FXML
  void onGroupByChanged(ActionEvent event) {}

  @FXML
  void onProjectChanged(ActionEvent event) {}

  @FXML
  void handleButtonClick(MouseEvent event) {}

  private boolean isButtonCloseClicked(ActionEvent event) {

    return event.getSource() == btClose || event.getSource() == imgClose;
  }

  private boolean isButtonLeftClicked(ActionEvent event) {

    return event.getSource() == btLeft || event.getSource() == imgMinimize12222;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {}

  @Override
  public void resetData() {

    lblPractice.setText("reseted to init state!");
  }

  @Override
  public void setData(StatsSceneData data) {
    // setea campos en la UI antes de show()
    lblPractice.setText(data.ninghtModeEnabled().toString());
  }

  @Override
  public StatsSceneData getData() {

    return new StatsSceneData(Boolean.TRUE);
  }
}
