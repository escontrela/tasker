package com.davidpe.tasker.domain.task;

import java.util.Objects;

public class Priority {

    private final Long id;
    private final String code;
    private final String description;

    public Priority(Long id, String code, String description) {
        this.id = id;
        this.code = Objects.requireNonNull(code, "code");
        this.description = Objects.requireNonNull(description, "description");
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
