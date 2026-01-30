package com.davidpe.tasker.application.ui.project;

import com.davidpe.tasker.application.ui.common.UiScreenController;
import com.davidpe.tasker.domain.project.Project;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewProjectPanelController extends UiScreenController implements NewProjectView {

  @FXML private Button btnCancel;

  @FXML private Button btnClose;

  @FXML private Button btnOk;

  @FXML private ImageView imgClose;

  @FXML private Label lblError;

  @FXML private Label lblName;

  @FXML private Label lblSubtitle;

  @FXML private Label lblTitle;

  @FXML private TextField txtTitle;

  private final NewProjectPresenter presenter;

  private NewProjectPanelData projectData;

  @Lazy
  public NewProjectPanelController(NewProjectPresenter presenter) {

    this.presenter = presenter;
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

    presenter.attach(this);
    resetData();
    presenter.loadInitialData();
  }

  @Override
  public void resetData() {

    lblError.setText("");
    lblSubtitle.setText("");
    txtTitle.clear();
  }

  private boolean isEditing() {

    return (projectData != null
        && projectData.getOperationType() == NewProjectPanelData.OperationType.EDIT
        && projectData.getProjectId() != null);
  }

  @Override
  public void setData(NewProjectPanelData data) {

    this.projectData = data;

    if (isEditing()) {

      presenter.loadProjectData();

    } else {

    }
  }

  @Override
  public NewProjectPanelData getData() {

    if (projectData != null) return projectData;
    return new NewProjectPanelData(NewProjectPanelData.OperationType.CREATE, null);
  }

  @Override
  public void close() {

    hideStage();
  }

  @Override
  public void populateProjectData(Project p) {}
}
