package com.davidpe.tasker.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void newUserInitializesFields() {
        String email = "user@example.com";

        User user = User.newUser(email);

        assertNull(user.getId());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getCreatedAt());
        assertEquals(0, Duration.between(user.getCreatedAt(), Instant.now()).abs().getSeconds());
    }

    @Test
    void constructorRejectsNullEmail() {
        Instant createdAt = Instant.now();

        NullPointerException exception = assertThrows(NullPointerException.class, () -> new User(1L, null, createdAt));

        assertEquals("email", exception.getMessage());
    }

    @Test
    void constructorRejectsNullCreatedAt() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> new User(1L, "user@example.com", null));

        assertEquals("createdAt", exception.getMessage());
    }
}
