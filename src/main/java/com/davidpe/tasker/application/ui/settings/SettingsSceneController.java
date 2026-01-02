package com.davidpe.tasker.application.ui.settings;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.davidpe.tasker.application.ui.common.UiControllerDataAware;
import com.davidpe.tasker.application.ui.common.UiScreen;
import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.application.ui.common.UiScreenFactory;
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.application.ui.events.WindowClosedEvent;
import com.davidpe.tasker.application.ui.tasks.NewTaskPanelData;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

@Component
public class SettingsSceneController  extends UiScreenController implements UiControllerDataAware<SettingsSceneData>{

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
  private Text lblChessboard;

  @FXML
  private Text lblPractice;

  @FXML
  private Pane mainPane;

  private final UiScreenFactory screenFactory;

  private ApplicationEventPublisher eventPublisher;

  @Lazy
  public SettingsSceneController(UiScreenFactory screenFactory,ApplicationEventPublisher eventPublisher) {

      this.eventPublisher = eventPublisher;
      this.screenFactory = screenFactory;
  }

  @FXML
  void buttonAction(ActionEvent event) {

    if (isButtonCloseClicked(event)) {

      lblPractice.setText("Bye.");
      eventPublisher.publishEvent(new WindowClosedEvent(UiScreenId.SETTINGS));

      //UiScreen returnToMainScreen = screenFactory.create(UiScreenId.MAIN);
      //returnToMainScreen.show();
      return;
    }

    if (isButtonLeftClicked(event)) {

      UiScreen newTaskModalScreen = screenFactory.create(UiScreenId.NEW_TASK_DIALOG);
      newTaskModalScreen.reset();
      newTaskModalScreen.setData(new NewTaskPanelData("Creating a new operation (from settings)?"));            
      newTaskModalScreen.show();
      return;
    }

  }

  @FXML
  void handleButtonClick(MouseEvent event) {

  }

  private boolean isButtonCloseClicked(ActionEvent event) {

    return event.getSource() == btClose || event.getSource() == imgClose;
  }

  private boolean isButtonLeftClicked(ActionEvent event) {

    return event.getSource() == btLeft || event.getSource() == imgMinimize12222;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  @Override
  public void resetData() {

    lblPractice.setText("reseted to init state!");
  }

  @Override
  public void setData(SettingsSceneData data) {
        // setea campos en la UI antes de show()
        lblPractice.setText(data.ninghtModeEnabled().toString());
  }

  @Override
  public SettingsSceneData getData() {
    
    return new SettingsSceneData(Boolean.TRUE);
  }

}