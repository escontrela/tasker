package com.davidpe.tasker.infrastructure.repository;

import com.davidpe.tasker.domain.agents.AgentRole;
import com.davidpe.tasker.domain.agents.AgentRoleRepository;
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
public class AgentRoleRepositoryImpl implements AgentRoleRepository {

  private final JdbcTemplate jdbcTemplate;
  private final RowMapper<AgentRole> mapper =
      (rs, rowNum) ->
          new AgentRole(
              rs.getLong("id"),
              rs.getString("code"),
              rs.getString("name"),
              rs.getTimestamp("created_at").toInstant());

  public AgentRoleRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<AgentRole> findAll() {
    return jdbcTemplate.query(
        "SELECT id, code, name, created_at FROM agent_roles ORDER BY name", mapper);
  }

  @Override
  public Optional<AgentRole> findById(Long id) {
    return jdbcTemplate
        .query("SELECT id, code, name, created_at FROM agent_roles WHERE id = ?", mapper, id)
        .stream()
        .findFirst();
  }

  @Override
  public Optional<AgentRole> findByCode(String code) {
    return jdbcTemplate
        .query("SELECT id, code, name, created_at FROM agent_roles WHERE code = ?", mapper, code)
        .stream()
        .findFirst();
  }

  @Override
  public AgentRole save(AgentRole role) {
    if (role.getId() == null) {
      return insert(role);
    }
    jdbcTemplate.update(
        "UPDATE agent_roles SET code = ?, name = ? WHERE id = ?",
        role.getCode(),
        role.getName(),
        role.getId());
    return role;
  }

  private AgentRole insert(AgentRole role) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(
        connection -> {
          PreparedStatement statement =
              connection.prepareStatement(
                  "INSERT INTO agent_roles (code, name, created_at) VALUES (?, ?, ?)",
                  Statement.RETURN_GENERATED_KEYS);
          statement.setString(1, role.getCode());
          statement.setString(2, role.getName());
          statement.setTimestamp(3, Timestamp.from(role.getCreatedAt()));
          return statement;
        },
        keyHolder);
    Number generatedId = keyHolder.getKey();
    return new AgentRole(
        generatedId == null ? null : generatedId.longValue(),
        role.getCode(),
        role.getName(),
        role.getCreatedAt());
  }
}
