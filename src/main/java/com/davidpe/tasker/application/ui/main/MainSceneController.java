package com.davidpe.tasker.application.ui.main;
 
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.davidpe.tasker.application.ui.common.FxmlView;
import com.davidpe.tasker.application.ui.common.StageManager;
import com.davidpe.tasker.application.ui.common.newer.Screen;
import com.davidpe.tasker.application.ui.common.newer.ScreenFactory;
import com.davidpe.tasker.application.ui.common.newer.ScreenId;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

@Component
public class MainSceneController implements Initializable {
    

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

    private final StageManager stageManager;
    private final ScreenFactory screenFactory;

    @Lazy
    public MainSceneController(StageManager stageManager,ScreenFactory screenFactory) {

        this.stageManager = stageManager;
        this.screenFactory = screenFactory;
    }

    @FXML
    void buttonAction(ActionEvent event) {

         if (isButtonCloseClicked(event)) {

            Screen settings = screenFactory.create(ScreenId.SETTINGS);
            settings.show();
            
            //TODO Send event to close;
            return;
         }

        if (isButtonSettingsClicked(event)) {

            stageManager.switchScene(FxmlView.SETTINGS);
            return;
         }


    }

    @FXML
    void handleButtonClick(MouseEvent event) {

        if (isButtonNewOpClicked(event)){
            stageManager.switchToNextParentScene(FxmlView.NEW_TASK);
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

    } 
}