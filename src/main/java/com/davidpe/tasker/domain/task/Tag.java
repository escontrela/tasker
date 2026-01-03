package com.davidpe.tasker.domain.task;

import java.util.Objects;

public class Tag {

    private final Long id;
    private final Long projectId;
    private final String name;

    public Tag(Long id, Long projectId, String name) {
        this.id = id;
        this.projectId = Objects.requireNonNull(projectId, "projectId");
        this.name = Objects.requireNonNull(name, "name");
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
