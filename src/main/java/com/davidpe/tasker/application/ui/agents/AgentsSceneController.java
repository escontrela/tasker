package com.davidpe.tasker.application.ui.agents;

import com.davidpe.tasker.application.agents.AddAgentCommand;
import com.davidpe.tasker.application.agents.AddAgentRoleCommand;
import com.davidpe.tasker.application.agents.AgentManagementService;
import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.application.ui.common.UiScreenId;
import com.davidpe.tasker.application.ui.controls.BreadcrumbBar;
import com.davidpe.tasker.application.ui.controls.BreadcrumbBar.BreadcrumbItem;
import com.davidpe.tasker.application.ui.events.WindowClosedEvent;
import com.davidpe.tasker.domain.agents.Agent;
import com.davidpe.tasker.domain.agents.AgentRole;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/** Manages the workspace directory of agents and the roles available to them. */
@Component
public class AgentsSceneController extends UiScreenController {

  private final ApplicationEventPublisher eventPublisher;
  private final AgentManagementService agentManagementService;

  @FXML private BreadcrumbBar breadcrumbBar;
  @FXML private TextField agentCodeField;
  @FXML private TextField agentNameField;
  @FXML private ComboBox<AgentRole> agentRoleCombo;
  @FXML private Label agentErrorLabel;
  @FXML private TextField roleCodeField;
  @FXML private TextField roleNameField;
  @FXML private Label roleErrorLabel;
  @FXML private VBox agentsList;
  @FXML private VBox rolesList;
  @FXML private Label agentCountLabel;

  @Lazy
  public AgentsSceneController(
      ApplicationEventPublisher eventPublisher, AgentManagementService agentManagementService) {
    this.eventPublisher = eventPublisher;
    this.agentManagementService = agentManagementService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    breadcrumbBar.setItems(
        BreadcrumbItem.link("Tasker", this::backToMain),
        BreadcrumbItem.current("Agents"),
        BreadcrumbItem.current("Directory"));
    configureRoleCombo();
    refreshDirectory();
  }

  @Override
  public void onShow() {
    refreshDirectory();
  }

  @FXML
  void addAgent() {
    try {
      agentManagementService.addAgent(
          new AddAgentCommand(
              agentCodeField.getText(),
              agentNameField.getText(),
              selectedRoleId()));
      agentCodeField.clear();
      agentNameField.clear();
      agentErrorLabel.setText("");
      refreshDirectory();
    } catch (IllegalArgumentException error) {
      agentErrorLabel.setText(error.getMessage());
    }
  }

  @FXML
  void addRole() {
    try {
      agentManagementService.addRole(
          new AddAgentRoleCommand(roleCodeField.getText(), roleNameField.getText()));
      roleCodeField.clear();
      roleNameField.clear();
      roleErrorLabel.setText("");
      refreshDirectory();
    } catch (IllegalArgumentException error) {
      roleErrorLabel.setText(error.getMessage());
    }
  }

  @FXML
  void backToMain() {
    eventPublisher.publishEvent(new WindowClosedEvent(UiScreenId.AGENTS));
  }

  @Override
  public void resetData() {
    agentCodeField.clear();
    agentNameField.clear();
    roleCodeField.clear();
    roleNameField.clear();
    agentErrorLabel.setText("");
    roleErrorLabel.setText("");
    refreshDirectory();
  }

  private Long selectedRoleId() {
    AgentRole role = agentRoleCombo.getValue();
    return role == null ? null : role.getId();
  }

  private void configureRoleCombo() {
    agentRoleCombo.setCellFactory(ignored -> roleCell());
    agentRoleCombo.setButtonCell(roleCell());
  }

  private ListCell<AgentRole> roleCell() {
    return new ListCell<>() {
      @Override
      protected void updateItem(AgentRole role, boolean empty) {
        super.updateItem(role, empty);
        setText(empty || role == null ? null : role.getName());
        getStyleClass().removeAll("agent-combo-cell-light", "agent-combo-cell-night");
        getStyleClass().add(isNightMode() ? "agent-combo-cell-night" : "agent-combo-cell-light");
      }
    };
  }

  private boolean isNightMode() {
    return getRootStage() != null
        && getRootStage().getScene() != null
        && getRootStage().getScene().getRoot().getStyleClass().contains("night-mode");
  }

  private void refreshDirectory() {
    List<AgentRole> roles = agentManagementService.getRoles();
    AgentRole selectedRole = agentRoleCombo.getValue();
    agentRoleCombo.getItems().setAll(roles);
    if (selectedRole != null && roles.contains(selectedRole)) {
      agentRoleCombo.setValue(selectedRole);
    } else if (!roles.isEmpty()) {
      agentRoleCombo.setValue(roles.getFirst());
    }
    renderRoles(roles);
    renderAgents(agentManagementService.getAgents());
  }

  private void renderRoles(List<AgentRole> roles) {
    rolesList.getChildren().setAll(roles.stream().map(this::roleRow).toList());
  }

  private void renderAgents(List<Agent> agents) {
    agentCountLabel.setText(agents.size() + (agents.size() == 1 ? " agent" : " agents"));
    if (agents.isEmpty()) {
      Label empty = new Label("No agents yet. Add the first member of your workspace above.");
      empty.getStyleClass().add("agent-empty-state");
      agentsList.getChildren().setAll(empty);
      return;
    }
    agentsList.getChildren().setAll(agents.stream().map(this::agentRow).toList());
  }

  private HBox roleRow(AgentRole role) {
    Label code = new Label(role.getCode());
    code.getStyleClass().add("agent-role-code");
    Label name = new Label(role.getName());
    name.getStyleClass().add("agent-row-title");
    HBox row = new HBox(10, code, name);
    row.getStyleClass().add("agent-role-row");
    return row;
  }

  private HBox agentRow(Agent agent) {
    Label code = new Label(agent.getCode());
    code.getStyleClass().add("agent-code-pill");
    Label name = new Label(agent.getName());
    name.getStyleClass().add("agent-row-title");
    Label role = new Label(agent.getRole().getName());
    role.getStyleClass().add("agent-role-pill");
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    HBox row = new HBox(14, code, name, spacer, role);
    row.getStyleClass().add("agent-directory-row");
    return row;
  }
}
