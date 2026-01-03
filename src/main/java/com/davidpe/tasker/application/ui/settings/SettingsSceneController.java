package com.davidpe.tasker.application.ui.settings;

import com.davidpe.tasker.application.ui.common.UiControllerDataAware;
import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.application.ui.events.WindowClosedEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class SettingsSceneController extends UiScreenController implements UiControllerDataAware<SettingsSceneData> {

    @FXML
    private Button btClose;

    @FXML
    private Label lblPractice;

    private final ApplicationEventPublisher eventPublisher;

    @Lazy
    public SettingsSceneController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @FXML
    void buttonAction(ActionEvent event) {
        if (event.getSource() == btClose) {
            lblPractice.setText("Bye.");
            eventPublisher.publishEvent(new WindowClosedEvent(UiScreenId.SETTINGS));
        }
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
    }

    @Override
    public SettingsSceneData getData() {
        return new SettingsSceneData(true);
    }
}
