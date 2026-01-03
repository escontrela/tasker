package com.davidpe.tasker.infrastructure.repository;

import com.davidpe.tasker.domain.task.Priority;
import com.davidpe.tasker.domain.task.PriorityRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PriorityRepositoryImpl implements PriorityRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Priority> mapper = (rs, rowNum) -> new Priority(
            rs.getLong("id"),
            rs.getString("code"),
            rs.getString("description")
    );

    public PriorityRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Priority> findAll() {
        String sql = "SELECT id, code, description FROM priorities ORDER BY id";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public Optional<Priority> findById(Long id) {
        String sql = "SELECT id, code, description FROM priorities WHERE id = ?";
        return jdbcTemplate.query(sql, mapper, id).stream().findFirst();
    }
}
