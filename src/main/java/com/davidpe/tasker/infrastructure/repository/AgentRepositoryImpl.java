package com.davidpe.tasker.infrastructure.repository;

import com.davidpe.tasker.domain.agents.Agent;
import com.davidpe.tasker.domain.agents.AgentRepository;
import com.davidpe.tasker.domain.agents.AgentRole;
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
public class AgentRepositoryImpl implements AgentRepository {

  private static final String AGENT_SELECT =
      "SELECT a.id AS agent_id, a.code AS agent_code, a.name AS agent_name, "
          + "a.created_at AS agent_created_at, r.id AS role_id, r.code AS role_code, "
          + "r.name AS role_name, r.created_at AS role_created_at "
          + "FROM agents a JOIN agent_roles r ON r.id = a.role_id";

  private final JdbcTemplate jdbcTemplate;
  private final RowMapper<Agent> mapper =
      (rs, rowNum) -> {
        AgentRole role =
            new AgentRole(
                rs.getLong("role_id"),
                rs.getString("role_code"),
                rs.getString("role_name"),
                rs.getTimestamp("role_created_at").toInstant());
        return new Agent(
            rs.getLong("agent_id"),
            rs.getString("agent_code"),
            rs.getString("agent_name"),
            role,
            rs.getTimestamp("agent_created_at").toInstant());
      };

  public AgentRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Agent> findAll() {
    return jdbcTemplate.query(AGENT_SELECT + " ORDER BY a.name", mapper);
  }

  @Override
  public Optional<Agent> findByCode(String code) {
    return jdbcTemplate.query(AGENT_SELECT + " WHERE a.code = ?", mapper, code).stream().findFirst();
  }

  @Override
  public Agent save(Agent agent) {
    if (agent.getId() == null) {
      return insert(agent);
    }
    jdbcTemplate.update(
        "UPDATE agents SET code = ?, name = ?, role_id = ? WHERE id = ?",
        agent.getCode(),
        agent.getName(),
        agent.getRole().getId(),
        agent.getId());
    return agent;
  }

  private Agent insert(Agent agent) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(
        connection -> {
          PreparedStatement statement =
              connection.prepareStatement(
                  "INSERT INTO agents (code, name, role_id, created_at) VALUES (?, ?, ?, ?)",
                  Statement.RETURN_GENERATED_KEYS);
          statement.setString(1, agent.getCode());
          statement.setString(2, agent.getName());
          statement.setLong(3, agent.getRole().getId());
          statement.setTimestamp(4, Timestamp.from(agent.getCreatedAt()));
          return statement;
        },
        keyHolder);
    Number generatedId = keyHolder.getKey();
    return new Agent(
        generatedId == null ? null : generatedId.longValue(),
        agent.getCode(),
        agent.getName(),
        agent.getRole(),
        agent.getCreatedAt());
  }
}
