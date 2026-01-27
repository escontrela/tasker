package com.davidpe.tasker.domain.stats;

import java.time.LocalDate;

/** Filters for task statistics. */
public final class TaskStatsQuery {

  private final long projectId;
  private final LocalDate from;
  private final LocalDate to;
  private final StatsAggregationLevel statsAggregationLevel;

  public TaskStatsQuery(
      long projectId, LocalDate from, LocalDate to, StatsAggregationLevel statsAggregationLevel) {
    this.projectId = projectId;
    this.from = from;
    this.to = to;
    this.statsAggregationLevel = statsAggregationLevel;
  }

  public long getProjectId() {
    return projectId;
  }

  public LocalDate getFrom() {
    return from;
  }

  public LocalDate getTo() {
    return to;
  }

  public StatsAggregationLevel getAggregationLevel() {
    return statsAggregationLevel;
  }
}
