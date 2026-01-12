package com.davidpe.tasker.application.task;

public class GetTaskCommand {

    private Long taskId; //TODO this should be a Value Object of the aggregate root Task

    public GetTaskCommand(Long taskId) {

        this.taskId = taskId;
    }

    public Long getTaskId() {

        return taskId;
    }
}