package com.davidpe.tasker.infrastructure.repository;

import com.davidpe.tasker.domain.user.User;
import com.davidpe.tasker.domain.user.UserRepository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> mapper = (rs, rowNum) ->
            new User(rs.getLong("id"), rs.getString("email"), rs.getTimestamp("created_at").toInstant());

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            return insert(user);
        }
        update(user);
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, email, created_at FROM users WHERE email = ?";
        return jdbcTemplate.query(sql, mapper, email).stream().findFirst();
    }

    private User insert(User user) {
        String sql = "INSERT INTO users (email, created_at) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getEmail());
            statement.setTimestamp(2, java.sql.Timestamp.from(user.getCreatedAt()));
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        Long id = generatedId != null ? generatedId.longValue() : null;
        return new User(id, user.getEmail(), user.getCreatedAt());
    }

    private void update(User user) {
        String sql = "UPDATE users SET email = ?, created_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), java.sql.Timestamp.from(user.getCreatedAt()), user.getId());
    }
}
