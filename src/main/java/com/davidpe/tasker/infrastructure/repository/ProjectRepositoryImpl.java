package com.davidpe.tasker.infrastructure.repository;

import com.davidpe.tasker.domain.project.Project;
import com.davidpe.tasker.domain.project.ProjectRepository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectRepositoryImpl implements ProjectRepository {

  private final JdbcTemplate jdbcTemplate;
  private final RowMapper<Project> mapper =
      (rs, rowNum) ->
          new Project(
              rs.getLong("id"),
              rs.getLong("user_id"),
              rs.getString("name"),
              rs.getTimestamp("created_at").toInstant());

  public ProjectRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Project> findAll() {
    String sql = "SELECT id, user_id, name, created_at FROM projects ORDER BY name";
    return jdbcTemplate.query(sql, mapper);
  }

  @Override
  public Optional<Project> findById(Long id) {
    String sql = "SELECT id, user_id, name, created_at FROM projects WHERE id = ?";
    return jdbcTemplate.query(sql, mapper, id).stream().findFirst();
  }

  @Override
  public List<Project> findByUserId(Long userId) {

    String sql =
        "SELECT id, user_id, name, created_at FROM projects WHERE user_id = ? ORDER BY name";
    return jdbcTemplate.query(sql, mapper, userId);
  }

  @Override
  public Project save(Project project) {
    if (project.getId() == null) {
      return insert(project);
    }
    update(project);
    return project;
  }

  private Project insert(Project project) {
    String sql = "INSERT INTO projects (user_id, name, created_at) VALUES (?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(
        connection -> {
          PreparedStatement statement =
              connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          statement.setLong(1, project.getUserId());
          statement.setString(2, project.getName());
          statement.setTimestamp(3, Timestamp.from(project.getCreatedAt()));
          return statement;
        },
        keyHolder);

    Number generatedId = keyHolder.getKey();
    Long id = generatedId != null ? generatedId.longValue() : null;
    return new Project(id, project.getUserId(), project.getName(), project.getCreatedAt());
  }

  private void update(Project project) {
    String sql = "UPDATE projects SET name = ? WHERE id = ?";
    jdbcTemplate.update(sql, project.getName(), project.getId());
  }
}
