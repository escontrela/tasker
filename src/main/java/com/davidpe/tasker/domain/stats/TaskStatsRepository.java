package com.davidpe.tasker.domain.stats;

import java.util.List;

public interface TaskStatsRepository {

  List<StatsPoint> getMetricSeries(TaskMetric metric, TaskStatsQuery query);
}
