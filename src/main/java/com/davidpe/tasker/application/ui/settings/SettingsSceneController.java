package com.davidpe.tasker.application.ui.settings;
 
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.davidpe.tasker.application.ui.common.FxmlView;
import com.davidpe.tasker.application.ui.common.StageManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button; 
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent; 
import javafx.scene.layout.Pane; 
import javafx.scene.text.Text;

@Component
public class SettingsSceneController implements Initializable {
    

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
   
    private final StageManager stageManager;

    @Lazy
    public SettingsSceneController(StageManager stageManager) {

        this.stageManager = stageManager;
    }
 

    @FXML
    void buttonAction(ActionEvent event) {

         if (isButtonCloseClicked(event)) {
 
            lblPractice.setText("Bye.");
            stageManager.switchToNextScene(FxmlView.MAIN);
            return;
         }
 


    }

    @FXML
    void handleButtonClick(MouseEvent event) {

       
    }
 

  private boolean isButtonCloseClicked(ActionEvent event) {

    return event.getSource() == btClose || event.getSource() == imgClose;
  }

 @Override
    public void initialize(URL location, ResourceBundle resources) {} 

}
