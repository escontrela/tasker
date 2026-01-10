package com.davidpe.tasker.application.ui.tasks;

import com.davidpe.tasker.domain.task.Task;

public class NewTaskPanelData {

    public enum OperationType {
        CREATE,
        EDIT
    }

    private OperationType operationType;
    private Task task;

    public NewTaskPanelData(OperationType operationType, Task task) {

        this.operationType = operationType;
        this.task = task;
    }


    public OperationType getOperationType() {

        return operationType;
    }

    public Task getTask() {

        return task;
    }
}