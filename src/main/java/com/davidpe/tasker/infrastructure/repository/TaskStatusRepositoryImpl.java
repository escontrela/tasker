package com.davidpe.tasker.infrastructure.repository;

import com.davidpe.tasker.domain.task.TaskStatus;
import com.davidpe.tasker.domain.task.TaskStatusRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TaskStatusRepositoryImpl implements TaskStatusRepository {

  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<TaskStatus> mapper =
      (rs, rowNum) -> new TaskStatus(rs.getLong("id"), rs.getString("code"));

  public TaskStatusRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<TaskStatus> findAll() {
    String sql = "SELECT id, code FROM task_status ORDER BY id";
    return jdbcTemplate.query(sql, mapper);
  }

  @Override
  public Optional<TaskStatus> findById(Long id) {
    String sql = "SELECT id, code FROM task_status WHERE id = ?";
    return jdbcTemplate.query(sql, mapper, id).stream().findFirst();
  }

  @Override
  public Optional<TaskStatus> findByCode(String code) {
    String sql = "SELECT id, code FROM task_status WHERE code = ?";
    return jdbcTemplate.query(sql, mapper, code).stream().findFirst();
  }
}
