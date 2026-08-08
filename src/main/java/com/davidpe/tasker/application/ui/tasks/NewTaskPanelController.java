package com.davidpe.tasker.application.ui.tasks;

import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.domain.agents.Agent;
import com.davidpe.tasker.domain.project.Project;
import com.davidpe.tasker.domain.task.Priority;
import com.davidpe.tasker.domain.task.Tag;
import com.davidpe.tasker.domain.task.Task;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewTaskPanelController extends UiScreenController implements NewTaskView {

  private static final double NORMAL_DESCRIPTION_LABEL_Y = 420;
  private static final double NORMAL_DESCRIPTION_Y = 445;
  private static final double NORMAL_DESCRIPTION_HEIGHT = 123;
  private static final double DESCRIPTION_WIDTH = 653;
  private static final double EXPANDED_DESCRIPTION_LABEL_Y = 188;
  private static final double EXPANDED_DESCRIPTION_Y = 213;
  private static final double EXPANDED_DESCRIPTION_HEIGHT = 355;

  @FXML private Button btnCancel;

  @FXML private Button btnOk;

  @FXML private ComboBox<Project> cbxProject;

  @FXML private ComboBox<Priority> cbxPriority;

  @FXML private ComboBox<Tag> cbxTag;

  @FXML private ComboBox<Agent> cbxAgent;

  @FXML private DatePicker dpEndDate;

  @FXML private DatePicker dpStartDate;

  @FXML private Label lblTitle;

  @FXML private Label lblSubtitle;

  @FXML private Label lblError;

  @FXML private Label lblProject;

  @FXML private Label lblPriority;

  @FXML private Label lblTag;

  @FXML private Label lblAgent;

  @FXML private Label lblStartDate;

  @FXML private Label lblEndDate;

  @FXML private Label lblDescription;

  @FXML private TextArea taDescription;

  @FXML private TextField txtExtCode;

  @FXML private TextField txtTitle;

  @FXML private Button btnClose;

  @FXML private ImageView imgClose;

  private final NewTaskPresenter presenter;

  private NewTaskPanelData taskData;

  @Lazy
  public NewTaskPanelController(NewTaskPresenter presenter) {

    this.presenter = presenter;
  }

  @FXML
  void buttonAction(ActionEvent event) {

    if (isButtonCancelClicked(event) || isButtonCloseClicked(event)) {
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

  private boolean isButtonCloseClicked(ActionEvent event) {

    return event.getSource() == btnClose || event.getSource() == imgClose;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    configureDialogButtons();
    configureDescriptionExpansion();
    configureAgentCombo();
    presenter.attach(this);
    resetData();
    presenter.loadInitialData();
  }

  private void configureDialogButtons() {
    btnCancel.getStyleClass().setAll("button", "message-box-button", "message-box-cancel-button");
    btnOk.getStyleClass().setAll("button", "message-box-button", "message-box-accept-button");
    btnClose.getStyleClass().setAll("button", "message-box-close-button");
  }

  private void configureDescriptionExpansion() {
    taDescription
        .focusedProperty()
        .addListener(
            (ignored, wasFocused, isFocused) -> setDescriptionExpanded(isFocused));
  }

  private void setDescriptionExpanded(boolean expanded) {
    metadataNodes().forEach(node -> node.setVisible(!expanded));
    lblDescription.setLayoutY(
        expanded ? EXPANDED_DESCRIPTION_LABEL_Y : NORMAL_DESCRIPTION_LABEL_Y);
    double y = expanded ? EXPANDED_DESCRIPTION_Y : NORMAL_DESCRIPTION_Y;
    double height = expanded ? EXPANDED_DESCRIPTION_HEIGHT : NORMAL_DESCRIPTION_HEIGHT;
    taDescription.setPrefHeight(height);
    taDescription.setMinHeight(height);
    taDescription.setMaxHeight(height);
    // Pane does not lay out children after their preferred size changes, so resize explicitly.
    taDescription.resizeRelocate(taDescription.getLayoutX(), y, DESCRIPTION_WIDTH, height);
    taDescription.requestLayout();
  }

  private List<Node> metadataNodes() {
    return List.of(
        lblProject,
        cbxProject,
        lblPriority,
        cbxPriority,
        lblTag,
        cbxTag,
        lblAgent,
        cbxAgent,
        lblStartDate,
        dpStartDate,
        lblEndDate,
        dpEndDate);
  }

  @Override
  public void resetData() {

    setDescriptionExpanded(false);
    lblError.setText("");
    lblSubtitle.setText("");
    txtTitle.clear();
    txtExtCode.clear();
    taDescription.clear();
    dpStartDate.setValue(null);
    dpEndDate.setValue(null);
    taskData = null;
    setAgentSelectorVisible(false);
  }

  private boolean isEditing() {

    return (taskData != null
        && taskData.getOperationType() == NewTaskPanelData.OperationType.EDIT
        && taskData.getTaskId() != null);
  }

  @Override
  public void setData(NewTaskPanelData data) {

    this.taskData = data;

    if (isEditing()) {
      setAgentSelectorVisible(true);
      presenter.loadTaskData();
    } else {
      setAgentSelectorVisible(false);
    }
  }

  @Override
  public NewTaskPanelData getData() {

    if (taskData != null) return taskData;
    return new NewTaskPanelData(NewTaskPanelData.OperationType.CREATE, null);
  }

  @Override
  public void showProjects(List<Project> projects) {

    cbxProject.getItems().setAll(projects);
    if (!projects.isEmpty()) {

      cbxProject.getSelectionModel().selectFirst();
    }
  }

  @Override
  public void selectProject(Long projectId) {

    if (projectId == null) return;

    cbxProject.getItems().stream()
        .filter(p -> p.getId().equals(projectId))
        .findFirst()
        .ifPresent(p -> cbxProject.getSelectionModel().select(p));
  }

  @Override
  public void showPriorities(List<Priority> priorities) {

    cbxPriority.getItems().setAll(priorities);
    if (!priorities.isEmpty()) {

      cbxPriority.getSelectionModel().selectFirst();
    }
  }

  @Override
  public void selectPriority(Long priorityId) {

    if (priorityId == null) return;

    cbxPriority.getItems().stream()
        .filter(p -> p.getId().equals(priorityId))
        .findFirst()
        .ifPresent(p -> cbxPriority.getSelectionModel().select(p));
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
  public void showAgents(List<Agent> agents) {
    cbxAgent.getItems().setAll(agents == null ? List.of() : agents);
    cbxAgent.getItems().add(0, null);
    cbxAgent.getSelectionModel().clearSelection();
  }

  @Override
  public void selectTag(Long tagId) {

    if (tagId == null) return;

    cbxTag.getItems().stream()
        .filter(t -> t.getId().equals(tagId))
        .findFirst()
        .ifPresent(t -> cbxTag.getSelectionModel().select(t));
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
  public Long selectedAgentId() {
    Agent agent = cbxAgent.getSelectionModel().getSelectedItem();
    return agent != null ? agent.getId() : null;
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

  @Override
  public void populateTaskData(Task t) {

    txtTitle.setText(t.getTitle());
    lblSubtitle.setText(t.getProjectId().toString());
    txtExtCode.setText(t.getExternalCode());
    taDescription.setText(t.getDescription());
    selectAgent(t.getAgentId());
    if (t.getStartAt() != null) {

      dpStartDate.setValue(
          java.time.LocalDate.ofInstant(t.getStartAt(), java.time.ZoneId.systemDefault()));
    } else {

      dpStartDate.setValue(null);
    }
    if (t.getEndAt() != null) {

      dpEndDate.setValue(
          java.time.LocalDate.ofInstant(t.getEndAt(), java.time.ZoneId.systemDefault()));
    } else {

      dpEndDate.setValue(null);
    }
  }

  @Override
  public void populateProjectOnSubtitle(String projectName) {

    lblSubtitle.setText(projectName);
  }

  @Override
  public void selectAgent(Long agentId) {
    if (agentId == null) {
      cbxAgent.getSelectionModel().clearSelection();
      return;
    }
    cbxAgent.getItems().stream()
        .filter(agent -> agent != null && agentId.equals(agent.getId()))
        .findFirst()
        .ifPresent(agent -> cbxAgent.getSelectionModel().select(agent));
  }

  private void configureAgentCombo() {
    cbxAgent.setPromptText("No agent");
    cbxAgent.setCellFactory(ignored -> agentCell());
    cbxAgent.setButtonCell(agentCell());
  }

  private javafx.scene.control.ListCell<Agent> agentCell() {
    return new javafx.scene.control.ListCell<>() {
      @Override
      protected void updateItem(Agent agent, boolean empty) {
        super.updateItem(agent, empty);
        setText(empty ? null : agent == null ? "No agent" : agent.getName());
      }
    };
  }

  private void setAgentSelectorVisible(boolean visible) {
    lblAgent.setVisible(visible);
    lblAgent.setManaged(visible);
    cbxAgent.setVisible(visible);
    cbxAgent.setManaged(visible);
  }
}
