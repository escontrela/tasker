package com.davidpe.tasker.application.ui.tasks;

import com.davidpe.tasker.application.ui.common.UiControllerDataAware;
import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.domain.project.Project;
import com.davidpe.tasker.domain.task.Priority;
import com.davidpe.tasker.domain.task.Tag;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewTaskPanelController extends UiScreenController implements UiControllerDataAware<NewTaskPanelData>, NewTaskView {

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnOk;

    @FXML
    private ComboBox<Project> cbxProject;

    @FXML
    private ComboBox<Priority> cbxPriority;

    @FXML
    private ComboBox<Tag> cbxTag;

    @FXML
    private DatePicker dpEndDate;

    @FXML
    private DatePicker dpStartDate;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblTitle;

    @FXML
    private Label lblError;

    @FXML
    private TextArea taDescription;

    @FXML
    private TextField txtExtCode;

    @FXML
    private TextField txtTitle;

    private final NewTaskPresenter presenter;

    @Lazy
    public NewTaskPanelController(NewTaskPresenter presenter) {
        this.presenter = presenter;
    }

    @FXML
    void buttonAction(ActionEvent event) {
        if (isButtonCancelClicked(event)) {
            hideStage();
            return;
        }
        if (isButtonOkClicked(event)) {
            presenter.onSaveRequested();
        }
    }

    @FXML
    void onProjectChanged(ActionEvent event) {
        presenter.onProjectChanged(selectedProjectId());
    }

    private boolean isButtonCancelClicked(ActionEvent event) {
        return event.getSource() == btnCancel;
    }

    private boolean isButtonOkClicked(ActionEvent event) {
        return event.getSource() == btnOk;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        presenter.attach(this);
        resetData();
        presenter.loadInitialData();
    }

    @Override
    public void resetData() {
        lblMessage.setText("The newest task.");
        lblError.setText("");
        txtTitle.clear();
        txtExtCode.clear();
        taDescription.clear();
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
    }

    @Override
    public void setData(NewTaskPanelData data) {
        lblMessage.setText(data.message());
    }

    @Override
    public NewTaskPanelData getData() {
        return new NewTaskPanelData(lblMessage.getText());
    }

    @Override
    public void showProjects(List<Project> projects) {
        cbxProject.getItems().setAll(projects);
        if (!projects.isEmpty()) {
            cbxProject.getSelectionModel().selectFirst();
        }
    }

    @Override
    public void showPriorities(List<Priority> priorities) {
        cbxPriority.getItems().setAll(priorities);
        if (!priorities.isEmpty()) {
            cbxPriority.getSelectionModel().selectFirst();
        }
    }

    @Override
    public void showTags(List<Tag> tags) {
        cbxTag.getItems().setAll(tags);
        if (!tags.isEmpty()) {
            cbxTag.getSelectionModel().selectFirst();
        } else {
            cbxTag.getSelectionModel().clearSelection();
        }
    }

    @Override
    public Long selectedProjectId() {
        Project project = cbxProject.getSelectionModel().getSelectedItem();
        return project != null ? project.getId() : null;
    }

    @Override
    public Long selectedPriorityId() {
        Priority priority = cbxPriority.getSelectionModel().getSelectedItem();
        return priority != null ? priority.getId() : null;
    }

    @Override
    public Long selectedTagId() {
        Tag tag = cbxTag.getSelectionModel().getSelectedItem();
        return tag != null ? tag.getId() : null;
    }

    @Override
    public String titleInput() {
        return txtTitle.getText();
    }

    @Override
    public String descriptionInput() {
        return taDescription.getText();
    }

    @Override
    public String externalCodeInput() {
        return txtExtCode.getText();
    }

    @Override
    public LocalDate startDate() {
        return dpStartDate.getValue();
    }

    @Override
    public LocalDate endDate() {
        return dpEndDate.getValue();
    }

    @Override
    public void showError(String message) {
        lblError.setText(message);
    }

    @Override
    public void close() {
        hideStage();
    }
}
