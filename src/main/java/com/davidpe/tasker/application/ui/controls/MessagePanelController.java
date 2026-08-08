package com.davidpe.tasker.application.ui.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Compact, reusable in-window notification that can be placed in any layout.
 */
public class MessagePanelController extends Pane {

    @FXML private Label lblTitle;
    @FXML private Label lblMessage;
    @FXML private Button btClose;

    private MessagePanelActionListener actionListener;

    /**
     * Listener retained for compatibility with existing callers.
     */
    public interface MessagePanelActionListener {
        void onOkButtonClicked();
        void onCancelButtonClicked();
    }

    public MessagePanelController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/davidpe/tasker/ui/controls/message-panel.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el FXML: /com/davidpe/tasker/ui/controls/message-panel.fxml", e);
        }
    }

    @FXML
    private void initialize() {
        if (lblMessage.getText() == null || lblMessage.getText().isEmpty()) {
            lblMessage.setText("Your workspace has been updated.");
        }
    }

    /**
     * Sets the message text to be displayed.
     *
     * @param message The message text
     */
    public void setMessage(String message) {
        lblMessage.setText(message);
    }

    /**
     * Gets the current message text.
     *
     * @return The current message text
     */
    public String getMessage() {
        return lblMessage.getText();
    }

    /** Sets the short heading displayed above the notification message. */
    public void setTitle(String title) {
        lblTitle.setText(title);
    }

    /**
     * Sets the action listener for button events.
     *
     * @param listener The action listener
     */
    public void setMessagePanelActionListener(MessagePanelActionListener listener) {
        this.actionListener = listener;
    }

    @FXML
    void buttonAction(ActionEvent event) {
        if (actionListener != null) {
            if (event.getSource() == btClose) {
                actionListener.onCancelButtonClicked();
            }
        }
    }
}
