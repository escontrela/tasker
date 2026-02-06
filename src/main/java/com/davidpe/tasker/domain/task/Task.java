package com.davidpe.tasker.domain.task;

import java.time.Instant;
import java.util.Objects;

public class Task {

    private final Long id;
    private final Long projectId;
    private final Long priorityId;
    private final Long tagId;
    private final String externalCode;
    private final String title;
    private final String description;
    private final Instant startAt;
    private final Instant endAt;
    private final java.math.BigDecimal sequence;
    private final TaskStatus taskStatus;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Task(Long id,
                Long projectId,
                Long priorityId,
                Long tagId,
                String externalCode,
                String title,
                String description,
                Instant startAt,
                Instant endAt,
                java.math.BigDecimal sequence,
                TaskStatus taskStatus,
                Instant createdAt,
                Instant updatedAt) {
        this.id = id;
        this.projectId = Objects.requireNonNull(projectId, "projectId");
        this.priorityId = Objects.requireNonNull(priorityId, "priorityId");
        this.tagId = tagId;
        this.externalCode = externalCode;
        this.title = Objects.requireNonNull(title, "title");
        this.description = Objects.requireNonNull(description, "description");
        this.startAt = startAt;
        this.endAt = endAt;
        this.sequence = sequence;
        this.taskStatus = Objects.requireNonNull(taskStatus, "taskStatus");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public static Task newTask(Long projectId,
                               Long priorityId,
                               Long tagId,
                               String externalCode,
                               String title,
                               String description,
                               Instant startAt,
                               Instant endAt,
                               TaskStatus taskStatus) {
        Instant now = Instant.now();
        return new Task(
            null,
            projectId,
            priorityId,
            tagId,
            externalCode,
            title,
            description,
            startAt,
            endAt,
            null,
            taskStatus,
            now,
            now);
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public Long getTagId() {
        return tagId;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public java.math.BigDecimal getSequence() {
        return sequence;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
