package com.davidpe.tasker.application.ui.common;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

/**
 * Base class for all screen controllers in the application.
 * It provides common functionality for initializing and resetting screen data.
 * Its is used by the screen factory to create and manage screen instances.
 */
public abstract class ScreenController implements Initializable {

        public abstract void initialize(URL location, ResourceBundle resources) ;
        public abstract void resetData();
}