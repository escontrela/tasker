package com.davidpe.tasker.application.ui.settings;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.davidpe.tasker.application.ui.common.ScreenController;
import com.davidpe.tasker.application.ui.common.newer.Screen;
import com.davidpe.tasker.application.ui.common.newer.ScreenFactory;
import com.davidpe.tasker.application.ui.common.newer.ScreenId;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

@Component
public class SettingsSceneController  extends ScreenController {

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

  private final ScreenFactory screenFactory;

  @Lazy
  public SettingsSceneController(ScreenFactory screenFactory) {

    this.screenFactory = screenFactory;
  }

  @FXML
  void buttonAction(ActionEvent event) {

    if (isButtonCloseClicked(event)) {


      System.out.println("Close button clicked and set text = bye!");
      lblPractice.setText("Bye.");

      Screen returnToMainScreen = screenFactory.create(ScreenId.MAIN);
      returnToMainScreen.show();
      return;
    }

    if (isButtonLeftClicked(event)) {

      System.out.println("Left button clicked"); 

      Screen newTaskModalScreen = screenFactory.create(ScreenId.NEW_TASK_DIALOG);
      newTaskModalScreen.reset();
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

}
