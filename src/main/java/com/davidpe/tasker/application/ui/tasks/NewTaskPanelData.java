package com.davidpe.tasker.application.ui.tasks;
 

public class NewTaskPanelData {

    public enum OperationType {
        CREATE,
        EDIT
    }

    private OperationType operationType;
    private Long taskId;

    public NewTaskPanelData(OperationType operationType, Long taskId) {

        this.operationType = operationType;
        this.taskId = taskId;
    }


    public OperationType getOperationType() {

        return operationType;
    }

    public Long getTaskId() {

        return taskId;
    }
}