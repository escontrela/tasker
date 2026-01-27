package com.davidpe.tasker.domain.stats;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;

public final class WeekPeriod implements TimePeriod {

  private final LocalDate startDate;

  public WeekPeriod(LocalDate startDate) {
    this.startDate = Objects.requireNonNull(startDate, "startDate must not be null");
  }

  @Override
  public String getLabel() {
    WeekFields weekFields = WeekFields.of(Locale.getDefault());
    int weekNumber = startDate.get(weekFields.weekOfWeekBasedYear());
    int year = startDate.get(weekFields.weekBasedYear());
    return String.format("W%02d %d", weekNumber, year);
  }

  @Override
  public LocalDate getStartDate() {
    return startDate;
  }
}
