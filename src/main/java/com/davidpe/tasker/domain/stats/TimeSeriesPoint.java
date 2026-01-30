package com.davidpe.tasker.domain.stats;

import java.util.Objects;

public final class TimeSeriesPoint {

  private final TimePeriod period;
  private final double value;

  public TimeSeriesPoint(TimePeriod period, double value) {
    this.period = Objects.requireNonNull(period, "period must not be null");
    this.value = value;
  }

  public TimePeriod getPeriod() {
    return period;
  }

  public double getValue() {
    return value;
  }
}
