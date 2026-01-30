package com.davidpe.tasker.infrastructure.repository;

import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskRepository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Task> mapper = (rs, rowNum) -> new Task(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("priority_id"),
            rs.getObject("tag_id") != null ? rs.getLong("tag_id") : null,
            rs.getString("external_code"),
            rs.getString("title"),
            rs.getString("description"),
            toInstant(rs.getTimestamp("start_at")),
            toInstant(rs.getTimestamp("end_at")),
            rs.getBigDecimal("sequence"),
            toBoolean(rs.getObject("done")),
            rs.getTimestamp("created_at").toInstant(),
            rs.getTimestamp("updated_at").toInstant()
    );

    public TaskRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            return insert(task);
        }
        update(task);
        return task;
    }

    @Override
    public List<Task> findAll() {
        String sql = "SELECT id, project_id, priority_id, tag_id, external_code, title, description, start_at, end_at, sequence, done, created_at, updated_at FROM tasks ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public List<Task> findAllByProjectId(Long projectId) {
        String sql = "SELECT id, project_id, priority_id, tag_id, external_code, title, description, start_at, end_at, sequence, done, created_at, updated_at FROM tasks WHERE project_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, mapper, projectId);
    }

    @Override
    public List<Task> findAllNotDone() {
        String sql = "SELECT id, project_id, priority_id, tag_id, external_code, title, description, start_at, end_at, sequence, done, created_at, updated_at FROM tasks WHERE done IS NULL OR done = 0 ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public List<Task> findAllNotDoneByProjectId(Long projectId) {
        String sql = "SELECT id, project_id, priority_id, tag_id, external_code, title, description, start_at, end_at, sequence, done, created_at, updated_at FROM tasks WHERE project_id = ? AND (done IS NULL OR done = 0) ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, mapper, projectId);
    }

    @Override
    public List<Task> findAllDone() {
        String sql = "SELECT id, project_id, priority_id, tag_id, external_code, title, description, start_at, end_at, sequence, done, created_at, updated_at FROM tasks WHERE done = 1 ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public List<Task> findAllDoneByProjectId(Long projectId) {
        String sql = "SELECT id, project_id, priority_id, tag_id, external_code, title, description, start_at, end_at, sequence, done, created_at, updated_at FROM tasks WHERE project_id = ? AND done = 1 ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, mapper, projectId);
    }

    private Task insert(Task task) {
        String sql = "INSERT INTO tasks (project_id, priority_id, tag_id, external_code, title, description, start_at, end_at, sequence, done, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, task.getProjectId());
            statement.setLong(2, task.getPriorityId());
            if (task.getTagId() != null) {
                statement.setLong(3, task.getTagId());
            } else {
                statement.setNull(3, Types.BIGINT);
            }
            statement.setString(4, task.getExternalCode());
            statement.setString(5, task.getTitle());
            statement.setString(6, task.getDescription());
            statement.setTimestamp(7, toTimestamp(task.getStartAt()));
            statement.setTimestamp(8, toTimestamp(task.getEndAt()));
            if (task.getSequence() != null) {
                statement.setBigDecimal(9, task.getSequence());
            } else {
                statement.setNull(9, Types.NUMERIC);
            }
            if (task.getDone() != null) {
                statement.setBoolean(10, task.getDone());
            } else {
                statement.setNull(10, Types.BOOLEAN);
            }
            statement.setTimestamp(11, Timestamp.from(task.getCreatedAt()));
            statement.setTimestamp(12, Timestamp.from(task.getUpdatedAt()));
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        Long id = generatedId != null ? generatedId.longValue() : null;
        return new Task(id,
                task.getProjectId(),
                task.getPriorityId(),
                task.getTagId(),
                task.getExternalCode(),
                task.getTitle(),
                task.getDescription(),
                task.getStartAt(),
                task.getEndAt(),
                task.getSequence(),
                task.getDone(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }

    private void update(Task task) {
        String sql = "UPDATE tasks SET project_id = ?, priority_id = ?, tag_id = ?, external_code = ?, title = ?, description = ?, start_at = ?, end_at = ?, sequence = ?, done = ?, created_at = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                task.getProjectId(),
                task.getPriorityId(),
                task.getTagId(),
                task.getExternalCode(),
                task.getTitle(),
                task.getDescription(),
                toTimestamp(task.getStartAt()),
                toTimestamp(task.getEndAt()),
                task.getSequence(),
                task.getDone(),
                Timestamp.from(task.getCreatedAt()),
                Timestamp.from(task.getUpdatedAt()),
                task.getId());
    }

    private static Timestamp toTimestamp(java.time.Instant instant) {
        return instant != null ? Timestamp.from(instant) : null;
    }

    private static java.time.Instant toInstant(Timestamp timestamp) {
        return timestamp != null ? timestamp.toInstant() : null;
    }

    private static Boolean toBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return Boolean.parseBoolean(value.toString());
    }

    @Override
    public Optional<Task> findById(Long taskId) { 

        String sql = "SELECT id, project_id, priority_id, tag_id, external_code, title, description, start_at, end_at, sequence, done, created_at, updated_at FROM tasks WHERE id = ?";
        List<Task> tasks = jdbcTemplate.query(sql, mapper, taskId);
        if (tasks.isEmpty()) {

            return Optional.empty();
        }
        return Optional.of(tasks.getFirst());
    }

    @Override
    public void deleteById(Long taskId) {
        
        String sql = "DELETE FROM tasks WHERE id = ?";
        jdbcTemplate.update(sql, taskId);
    }
}
