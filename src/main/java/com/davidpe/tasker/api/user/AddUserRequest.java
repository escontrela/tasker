package com.davidpe.tasker.api.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddUserRequest(@NotBlank @Email String email) { }
