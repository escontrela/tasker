package com.davidpe.tasker.application.stats;

import com.davidpe.tasker.domain.stats.DayPeriod;
import com.davidpe.tasker.domain.stats.MonthPeriod;
import com.davidpe.tasker.domain.stats.StatsAggregationLevel;
import com.davidpe.tasker.domain.stats.StatsPoint;
import com.davidpe.tasker.domain.stats.TaskStatsQuery;
import com.davidpe.tasker.domain.stats.TaskStatsRepository;
import com.davidpe.tasker.domain.stats.TimePeriod;
import com.davidpe.tasker.domain.stats.TimeSeries;
import com.davidpe.tasker.domain.stats.TimeSeriesDataset;
import com.davidpe.tasker.domain.stats.WeekPeriod;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class GetTaskMetricTimeSeriesDatasetUseCase {

  private final TaskStatsRepository taskStatsRepository;

  public GetTaskMetricTimeSeriesDatasetUseCase(TaskStatsRepository taskStatsRepository) {
    this.taskStatsRepository = taskStatsRepository;
  }

  public TimeSeriesDataset execute(GetTaskMetricTimeSeriesDatasetRequest request) {
    Objects.requireNonNull(request, "request must not be null");
    TaskStatsQuery query = request.getQuery();

    System.out.println(
        "GetTaskMetricTimeSeriesDatasetUseCase.execute: metric="
            + request.getMetric()
            + ", projectId="
            + query.getProjectId()
            + ", from="
            + query.getFrom()
            + ", to="
            + query.getTo()
            + ", aggregationLevel="
            + query.getAggregationLevel());

    List<StatsPoint> points = taskStatsRepository.getMetricSeries(request.getMetric(), query);

    System.out.println("GetTaskMetricTimeSeriesDatasetUseCase.execute: points=" + points.size());

    TimeSeries series = new TimeSeries("Tasks created");
    for (StatsPoint point : points) {
      TimePeriod period = toPeriod(point.getPeriodStart(), query.getAggregationLevel());
      series.add(period, point.getValue());
    }
    TimeSeriesDataset dataset = new TimeSeriesDataset();
    dataset.addSeries(series);
    return dataset;
  }

  private TimePeriod toPeriod(LocalDate periodStart, StatsAggregationLevel aggregationLevel) {
    return switch (aggregationLevel) {
      case DAILY -> new DayPeriod(periodStart);
      case WEEKLY -> new WeekPeriod(periodStart);
      case MONTHLY -> new MonthPeriod(periodStart);
    };
  }
}
