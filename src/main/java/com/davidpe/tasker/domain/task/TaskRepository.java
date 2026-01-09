package com.davidpe.tasker.domain.task;

import java.util.List;

public interface TaskRepository {

    Task save(Task task);

    List<Task> findAll();
}
