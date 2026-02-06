package com.davidpe.tasker.domain.task;

import java.util.List;
import java.util.Optional;

public interface TaskStatusRepository {

  List<TaskStatus> findAll();

  Optional<TaskStatus> findById(Long id);

  Optional<TaskStatus> findByCode(String code);
}
