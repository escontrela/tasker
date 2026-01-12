package com.davidpe.tasker.application.task;

import org.springframework.stereotype.Service;

import com.davidpe.tasker.domain.task.TaskRepository;

@Service
public class DeleteTaskUseCase {

    private final TaskRepository taskRepository;

    public DeleteTaskUseCase(TaskRepository taskRepository) {

        this.taskRepository = taskRepository;
    }

    public void deleteTask(DeleteTaskCommand command) {

        taskRepository.deleteById(command.taskId());
    }

}
