package com.davidpe.tasker.domain.task;

import java.util.List;
import java.util.Optional;

public interface TagRepository {

    List<Tag> findByProjectId(Long projectId);

    Optional<Tag> findById(Long id);
}
