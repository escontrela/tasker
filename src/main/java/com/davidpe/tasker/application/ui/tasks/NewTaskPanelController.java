package com.davidpe.tasker.application.ui.tasks;
 
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.davidpe.tasker.application.ui.common.FxmlView;
import com.davidpe.tasker.application.ui.common.ScreenController;
import com.davidpe.tasker.application.ui.common.StageManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window; 

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewTaskPanelController  extends ScreenController{

    @FXML
    private Pane mainPane;
    
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

            if (mainPane.getScene() == null) return; // aún no está en una Scene

            Window window = mainPane.getScene().getWindow();
            if (window instanceof Stage) {
                Stage stage = (Stage) window;
                stage.hide();
            }

            System.out.println("Cancel button clicked, switching to main scene");
            return;
         }

    }
 
    private boolean isButtonCancelClicked(ActionEvent event) {

      return event.getSource() == btCancel || event.getSource() == imgCancel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        lblMessage1.setText("The newest task.");
        System.out.println("Instance class:" + this);
    }


    @Override
    public void resetData() {

       lblMessage1.setText(Thread.currentThread().getName() + ": Reset data called in NewTaskPanelController instance " + this);
    } 

}