package com.davidpe.tasker.domain.task;

import java.util.List;
import java.util.Optional;

public interface PriorityRepository {

    List<Priority> findAll();

    Optional<Priority> findById(Long id);
}
