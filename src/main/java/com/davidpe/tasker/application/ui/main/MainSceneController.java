package com.davidpe.tasker.application.ui.main;
 
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.davidpe.tasker.application.ui.common.Screen;
import com.davidpe.tasker.application.ui.common.ScreenController;
import com.davidpe.tasker.application.ui.common.ScreenFactory;
import com.davidpe.tasker.application.ui.common.ScreenId;
import com.davidpe.tasker.application.ui.settings.SettingsSceneData;
import com.davidpe.tasker.application.ui.tasks.NewTaskPanelData;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXML; 
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

@Component
public class MainSceneController extends ScreenController {
    
    private double xOffset = 0;
    private double yOffset = 0;

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
    private ImageView imgSettings;

    @FXML
    private Label lbUserInitials;

    @FXML
    private Text lblChessboard;

    @FXML
    private Text lblPractice;

    @FXML
    private Pane mainPane;

    @FXML
    private StackPane pnlMenu;

    @FXML
    private Pane pnlMessage11;

    @FXML
    private HBox taskBar;

    @FXML
    private StackPane taskOption_analysis;

    @FXML
    private StackPane taskOption_games;

    @FXML
    private StackPane taskOption_settings;

    @FXML
    private StackPane taskOption_stats;

    @FXML
    private Label lblNewOp;

    private final ScreenFactory screenFactory;

    @Lazy
    public MainSceneController(ScreenFactory screenFactory) {

        this.screenFactory = screenFactory;
    }

    @FXML
    void buttonAction(ActionEvent event) {

         if (isButtonCloseClicked(event)) {

            if (mainPane.getScene() == null) return; 
            Window window = mainPane.getScene().getWindow();
            if (window instanceof Stage) {
                Stage stage = (Stage) window;
                // usar stage...
                stage.setTitle("Nuevo título");
                stage.hide();
            }
           
            //TODO Send event to close;
            return;
         }

        if (isButtonSettingsClicked(event)) {

            Screen settings = screenFactory.create(ScreenId.SETTINGS);
            settings.reset();
            settings.setData(new SettingsSceneData(Boolean.TRUE));
            settings.show();
            return;
         }
    }

    @FXML
    void handleButtonClick(MouseEvent event) {

        if (isButtonNewOpClicked(event)){

            Screen newTaskDialog = screenFactory.create(ScreenId.NEW_TASK_DIALOG);
            newTaskDialog.setData(new NewTaskPanelData("Creating a new operation?"));
            newTaskDialog.show();
            return;
        }
    }

    private boolean isButtonSettingsClicked(ActionEvent event) {

        return event.getSource() == btSettings || event.getSource() == imgSettings;
    }

    private boolean isButtonNewOpClicked(MouseEvent event) {

        return event.getSource() == lblNewOp;
    }

    private boolean isButtonCloseClicked(ActionEvent event) {

        return event.getSource() == btClose || event.getSource() == imgClose;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        moveMainWindowsSetUp();
    }

    @Override
    public void resetData() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resetData'");
    } 

     private void moveMainWindowsSetUp() {
         
        mainPane.setOnMousePressed(
                event -> {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                });

        
        mainPane.setOnMouseDragged(
                event -> {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
                });
  }
}