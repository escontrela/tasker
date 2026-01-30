package com.davidpe.tasker.application.stats;

import com.davidpe.tasker.domain.stats.TaskMetric;
import com.davidpe.tasker.domain.stats.TaskStatsQuery;
import java.util.Objects;

public final class GetTaskMetricTimeSeriesDatasetRequest {

  private final TaskMetric metric;
  private final TaskStatsQuery query;

  public GetTaskMetricTimeSeriesDatasetRequest(TaskMetric metric, TaskStatsQuery query) {
    this.metric = Objects.requireNonNull(metric, "metric must not be null");
    this.query = Objects.requireNonNull(query, "query must not be null");
  }

  public TaskMetric getMetric() {
    return metric;
  }

  public TaskStatsQuery getQuery() {
    return query;
  }
}
