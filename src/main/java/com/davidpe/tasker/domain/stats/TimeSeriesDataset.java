package com.davidpe.tasker.domain.stats;

import java.util.ArrayList;
import java.util.List;

public final class TimeSeriesDataset {

  private final List<TimeSeries> series = new ArrayList<>();

  public void addSeries(TimeSeries timeSeries) {
    series.add(timeSeries);
  }

  public List<TimeSeries> getSeries() {
    return List.copyOf(series);
  }

  public boolean isEmpty() {
    return series.isEmpty();
  }
}
