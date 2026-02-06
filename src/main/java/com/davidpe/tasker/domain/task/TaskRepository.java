package com.davidpe.tasker.domain.task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task save(Task task);

    List<Task> findAll();

    List<Task> findAllByProjectId(Long projectId);

    List<Task> findAllByStatusCode(String statusCode);

    List<Task> findAllByProjectIdAndStatusCode(Long projectId, String statusCode);

    Optional<Task> findById(Long taskId);

    void deleteById(Long taskId);
}
