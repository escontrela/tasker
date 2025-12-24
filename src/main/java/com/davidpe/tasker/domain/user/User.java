package com.davidpe.tasker.domain.user;

import java.time.Instant;
import java.util.Objects;

public class User {

    private final Long id;
    private final String email;
    private final Instant createdAt;

    public User(Long id, String email, Instant createdAt) {
        this.id = id;
        this.email = Objects.requireNonNull(email, "email");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static User newUser(String email) {
        return new User(null, email, Instant.now());
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
