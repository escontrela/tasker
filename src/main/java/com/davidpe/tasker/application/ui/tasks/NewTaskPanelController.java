package com.davidpe.tasker.application.ui.tasks;
 
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
import javafx.scene.control.Label;
import javafx.scene.image.ImageView; 

@Component
public class NewTaskPanelController implements Initializable {

    @FXML
    private Button btCancel;

    @FXML
    private Button btOk;

    @FXML
    private ImageView imgIcon;

    @FXML
    private ImageView imgCancel;

    @FXML
    private ImageView imgOK;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblMessage1;

    private final StageManager stageManager;

    @Lazy
    public NewTaskPanelController(StageManager stageManager) {

        this.stageManager = stageManager;
    }

  
    @FXML
    void buttonAction(ActionEvent event) {

          System.out.println("Button clicked");
         if (isButtonCancelClicked(event)) {

            System.out.println("Cancel button clicked, switching to main scene");
            stageManager.switchToNextScene(FxmlView.MAIN);
            return;
         }

    }
 
    private boolean isButtonCancelClicked(ActionEvent event) {

      return event.getSource() == btCancel || event.getSource() == imgCancel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        lblMessage1.setText("The newest task.");

    } 

}