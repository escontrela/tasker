package com.davidpe.tasker.application.ui.tasks;
 
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.davidpe.tasker.application.ui.common.UiControllerDataAware;
import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.application.ui.common.UiScreenFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewTaskPanelController  extends UiScreenController implements UiControllerDataAware<NewTaskPanelData>{

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

    private UiScreenFactory screenFactory;

    @Lazy
    public NewTaskPanelController(UiScreenFactory screenFactory) {

            this.screenFactory = screenFactory;
    }

  
    @FXML
    void buttonAction(ActionEvent event) {


         if (isButtonCancelClicked(event)) {

            hideStage();
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


    @Override
    public void resetData() {

       lblMessage1.setText(Thread.currentThread().getName() + ": Reset data called in NewTaskPanelController instance " + this);
    }


    @Override
    public void setData(NewTaskPanelData data) {
        
        lblMessage.setText(data.message());
    }


    @Override
    public NewTaskPanelData getData() {
        
        return new NewTaskPanelData(lblMessage.getText());
    }
}