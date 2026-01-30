package com.davidpe.tasker.domain.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TimeSeries {

  private final String name;
  private final List<TimeSeriesPoint> points = new ArrayList<>();

  public TimeSeries(String name) {
    this.name = Objects.requireNonNull(name, "name must not be null");
  }

  public String getName() {
    return name;
  }

  public List<TimeSeriesPoint> getPoints() {
    return List.copyOf(points);
  }

  public void add(TimePeriod period, double value) {
    points.add(new TimeSeriesPoint(period, value));
  }
}
