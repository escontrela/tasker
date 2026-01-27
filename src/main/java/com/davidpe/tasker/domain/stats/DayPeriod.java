package com.davidpe.tasker.domain.stats;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class DayPeriod implements TimePeriod {

  private static final DateTimeFormatter LABEL_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

  private final LocalDate startDate;

  public DayPeriod(LocalDate startDate) {
    this.startDate = Objects.requireNonNull(startDate, "startDate must not be null");
  }

  @Override
  public String getLabel() {
    return startDate.format(LABEL_FORMAT);
  }

  @Override
  public LocalDate getStartDate() {
    return startDate;
  }
}
