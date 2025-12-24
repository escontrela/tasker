package com.davidpe.tasker.api.user;

import java.time.Instant;

public record AddUserResponse(Long id, String email, Instant createdAt) { }
