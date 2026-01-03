package com.davidpe.tasker.application.ui.main;
 
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.davidpe.tasker.application.ui.common.UiScreen;
import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.application.ui.common.UiScreenFactory;
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.application.ui.events.WindowClosedEvent;
import com.davidpe.tasker.application.ui.events.WindowOpenedEvent;
import com.davidpe.tasker.application.ui.settings.SettingsSceneData;
import com.davidpe.tasker.application.ui.tasks.NewTaskPanelData;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXML; 
import javafx.scene.control.Button;
import javafx.scene.control.Label; 
import java.awt.Point;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class MainSceneController extends UiScreenController {
    
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

    private final UiScreenFactory screenFactory;

    private ApplicationEventPublisher eventPublisher;

    @Lazy
    public MainSceneController(UiScreenFactory screenFactory, ApplicationEventPublisher eventPublisher) {

        this.screenFactory = screenFactory;
        this.eventPublisher = eventPublisher;
    }

    @FXML
    void buttonAction(ActionEvent event) {

         if (isButtonCloseClicked(event)) {

            hideStage();
             
            //TODO Send event to close;
            return;
         }

        if (isButtonSettingsClicked(event)) {

            UiScreen settings = screenFactory.create(UiScreenId.SETTINGS);
            settings.reset();
            settings.setData(new SettingsSceneData(Boolean.TRUE));
            settings.show();
            return;
         }
    }

    @FXML
    void handleButtonClick(MouseEvent event) {

        if (isButtonNewOpClicked(event)){

            eventPublisher.publishEvent(new WindowOpenedEvent(UiScreenId.NEW_TASK_DIALOG));

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