package com.davidpe.tasker.domain.task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task save(Task task);

    List<Task> findAll();

    List<Task> findAllByProjectId(Long projectId);

    List<Task> findAllByStatusCode(String statusCode);

    List<Task> findAllByProjectIdAndStatusCode(Long projectId, String statusCode);

    List<Task> findAllByProjectIdAndStatusCodeOrderByStartAtAsc(Long projectId, String statusCode);

    List<Task> findAllByProjectIdAndStatusCodeAndUserIdOrderByStartAtAsc(Long projectId, String statusCode, Long userId);

    Optional<Task> findById(Long taskId);

    Optional<Task> findByIdAndProjectId(Long taskId, Long projectId);

    Optional<Task> findByIdAndProjectIdAndUserId(Long taskId, Long projectId, Long userId);

    void deleteById(Long taskId);
}
