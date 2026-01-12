package com.davidpe.tasker.application.task;

import org.springframework.stereotype.Service;

import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskRepository;

@Service
public class GetTaskUseCase {

    private final TaskRepository taskRepository;

    public GetTaskUseCase(TaskRepository taskRepository) {

        this.taskRepository = taskRepository;
    }

    public Task getTaskById(GetTaskCommand command) {

        Long taskId = command.getTaskId();
        
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
    }
}
