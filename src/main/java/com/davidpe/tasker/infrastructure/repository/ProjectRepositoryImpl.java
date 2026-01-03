package com.davidpe.tasker.infrastructure.repository;

import com.davidpe.tasker.domain.project.Project;
import com.davidpe.tasker.domain.project.ProjectRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectRepositoryImpl implements ProjectRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Project> mapper = (rs, rowNum) -> new Project(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("name"),
            rs.getTimestamp("created_at").toInstant()
    );

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
}
