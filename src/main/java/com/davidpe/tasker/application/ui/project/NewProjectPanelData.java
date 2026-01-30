package com.davidpe.tasker.application.ui.project;

public class NewProjectPanelData {

  public enum OperationType {
    CREATE,
    EDIT
  }

  private OperationType operationType;
  private Long projectId;

  public NewProjectPanelData(OperationType operationType, Long projectId) {

    this.operationType = operationType;
    this.projectId = projectId;
  }

  public OperationType getOperationType() {

    return operationType;
  }

  public Long getProjectId() {

    return projectId;
  }
}
