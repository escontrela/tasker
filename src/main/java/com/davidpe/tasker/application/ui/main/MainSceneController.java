package com.davidpe.tasker.application.ui.main;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class MainSceneController {

    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {
        if (titleLabel != null) {
            titleLabel.setText("Tasker Main Screen");
        }
    }
}
