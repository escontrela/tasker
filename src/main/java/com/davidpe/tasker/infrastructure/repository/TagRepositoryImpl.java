package com.davidpe.tasker.infrastructure.repository;

import com.davidpe.tasker.domain.task.Tag;
import com.davidpe.tasker.domain.task.TagRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TagRepositoryImpl implements TagRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Tag> mapper = (rs, rowNum) -> new Tag(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getString("name")
    );

    public TagRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Tag> findByProjectId(Long projectId) {
        String sql = "SELECT id, project_id, name FROM tags WHERE project_id = ? ORDER BY name";
        return jdbcTemplate.query(sql, mapper, projectId);
    }

    @Override
    public Optional<Tag> findById(Long id) {
        String sql = "SELECT id, project_id, name FROM tags WHERE id = ?";
        return jdbcTemplate.query(sql, mapper, id).stream().findFirst();
    }
}
