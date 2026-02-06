package com.davidpe.tasker.infrastructure.repository;

import com.davidpe.tasker.domain.task.Task;
import com.davidpe.tasker.domain.task.TaskRepository;
import com.davidpe.tasker.domain.task.TaskStatus;
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

  private static final String SELECT_TASK_FIELDS =
      "SELECT t.id, t.project_id, t.priority_id, t.tag_id, t.external_code, t.title, t.description, "
          + "t.start_at, t.end_at, t.sequence, t.task_status_id, t.created_at, t.updated_at, ts.code AS task_status_code "
          + "FROM tasks t "
          + "JOIN task_status ts ON ts.id = t.task_status_id ";

  private final JdbcTemplate jdbcTemplate;
  private final RowMapper<Task> mapper =
      (rs, rowNum) ->
          new Task(
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
              new TaskStatus(rs.getLong("task_status_id"), rs.getString("task_status_code")),
              rs.getTimestamp("created_at").toInstant(),
              rs.getTimestamp("updated_at").toInstant());

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
    String sql = SELECT_TASK_FIELDS + "ORDER BY t.created_at DESC";
    return jdbcTemplate.query(sql, mapper);
  }

  @Override
  public List<Task> findAllByProjectId(Long projectId) {
    String sql = SELECT_TASK_FIELDS + "WHERE t.project_id = ? ORDER BY t.created_at DESC";
    return jdbcTemplate.query(sql, mapper, projectId);
  }

  @Override
  public List<Task> findAllByStatusCode(String statusCode) {
    String sql = SELECT_TASK_FIELDS + "WHERE ts.code = ? ORDER BY t.created_at DESC";
    return jdbcTemplate.query(sql, mapper, statusCode);
  }

  @Override
  public List<Task> findAllByProjectIdAndStatusCode(Long projectId, String statusCode) {
    String sql =
        SELECT_TASK_FIELDS + "WHERE t.project_id = ? AND ts.code = ? ORDER BY t.created_at DESC";
    return jdbcTemplate.query(sql, mapper, projectId, statusCode);
  }

  private Task insert(Task task) {
    String sql =
        "INSERT INTO tasks (project_id, priority_id, tag_id, external_code, title, description, start_at, end_at, sequence, task_status_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(
        connection -> {
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
          statement.setLong(10, task.getTaskStatus().getId());
          statement.setTimestamp(11, Timestamp.from(task.getCreatedAt()));
          statement.setTimestamp(12, Timestamp.from(task.getUpdatedAt()));
          return statement;
        },
        keyHolder);

    Number generatedId = keyHolder.getKey();
    Long id = generatedId != null ? generatedId.longValue() : null;
    return new Task(
        id,
        task.getProjectId(),
        task.getPriorityId(),
        task.getTagId(),
        task.getExternalCode(),
        task.getTitle(),
        task.getDescription(),
        task.getStartAt(),
        task.getEndAt(),
        task.getSequence(),
        task.getTaskStatus(),
        task.getCreatedAt(),
        task.getUpdatedAt());
  }

  private void update(Task task) {
    String sql =
        "UPDATE tasks SET project_id = ?, priority_id = ?, tag_id = ?, external_code = ?, title = ?, description = ?, start_at = ?, end_at = ?, sequence = ?, task_status_id = ?, created_at = ?, updated_at = ? WHERE id = ?";
    jdbcTemplate.update(
        sql,
        task.getProjectId(),
        task.getPriorityId(),
        task.getTagId(),
        task.getExternalCode(),
        task.getTitle(),
        task.getDescription(),
        toTimestamp(task.getStartAt()),
        toTimestamp(task.getEndAt()),
        task.getSequence(),
        task.getTaskStatus().getId(),
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

  @Override
  public Optional<Task> findById(Long taskId) {
    String sql = SELECT_TASK_FIELDS + "WHERE t.id = ?";
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
