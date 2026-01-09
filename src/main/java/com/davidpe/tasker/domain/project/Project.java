package com.davidpe.tasker.domain.project;

import java.time.Instant;
import java.util.Objects;

public class Project {

    private final Long id;
    private final Long userId;
    private final String name;
    private final Instant createdAt;

    public Project(Long id, Long userId, String name, Instant createdAt) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "userId");
        this.name = Objects.requireNonNull(name, "name");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return name;
    }
}
