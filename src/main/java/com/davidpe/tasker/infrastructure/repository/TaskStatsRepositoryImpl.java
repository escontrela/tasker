package com.davidpe.tasker.infrastructure.repository;

import com.davidpe.tasker.domain.stats.StatsAggregationLevel;
import com.davidpe.tasker.domain.stats.StatsPoint;
import com.davidpe.tasker.domain.stats.TaskMetric;
import com.davidpe.tasker.domain.stats.TaskStatsQuery;
import com.davidpe.tasker.domain.stats.TaskStatsRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TaskStatsRepositoryImpl implements TaskStatsRepository {

  private final JdbcTemplate jdbcTemplate;

  private final RowMapper<StatsPoint> statsPointMapper =
      (rs, rowNum) -> {
        LocalDate periodStart = LocalDate.parse(rs.getString("period_start"));
        double value = rs.getDouble("value");
        return new StatsPoint(periodStart, value);
      };

  public TaskStatsRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<StatsPoint> getMetricSeries(TaskMetric metric, TaskStatsQuery query) {
    String periodExpression = periodExpression(query.getAggregationLevel());
    String sql =
        """
        SELECT %s AS period_start,
               COUNT(*) AS value
        FROM tasks
        GROUP BY period_start
        ORDER BY period_start
        """
            .formatted(periodExpression);

    return switch (metric) {
      case CREATED_TASKS -> jdbcTemplate.query(sql, statsPointMapper);
      // Date.valueOf(query.getFrom()),
      // Date.valueOf(query.getTo()));
      case OPEN_TASKS, DONE_TASKS -> List.of();
    };
  }

  private String periodExpression(StatsAggregationLevel level) {
    return switch (level) {
      case DAILY -> "date(start_at / 1000, 'unixepoch')";
      case WEEKLY -> "date(start_at / 1000, 'unixepoch', 'weekday 1', '-7 days')";
      case MONTHLY -> "date(start_at / 1000, 'unixepoch', 'start of month')";
    };
  }
}
