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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewTaskPanelController  extends UiScreenController implements UiControllerDataAware<NewTaskPanelData>{

     @FXML
    private Button btnCancel;

    @FXML
    private Button btnFilter;

    @FXML
    private Button btnOk;

    @FXML
    private ComboBox<?> cbxPriority;

    @FXML
    private ComboBox<?> cbxTag;

    @FXML
    private DatePicker dpStartDate;

    @FXML
    private ImageView imgCancel;

    @FXML
    private ImageView imgIcon;

    @FXML
    private ImageView imgOk;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblTitle;

    @FXML
    private Pane paneMain;

    @FXML
    private TextArea taDescription;

    @FXML
    private TextField txtExtCode;

    @FXML
    private TextField txtTitle;

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

      return event.getSource() == btnCancel || event.getSource() == imgCancel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        lblMessage.setText("The newest task.");
    }


    @Override
    public void resetData() {

       lblMessage.setText(Thread.currentThread().getName() + ": Reset data called in NewTaskPanelController instance " + this);
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