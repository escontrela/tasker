package com.davidpe.tasker.domain.stats;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public final class MonthPeriod implements TimePeriod {

  private static final DateTimeFormatter LABEL_FORMAT =
      DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault());

  private final int month;
  private final int year;
  private final LocalDate startDate;

  public MonthPeriod(int month, int year) {
    this.month = month;
    this.year = year;
    this.startDate = LocalDate.of(year, month, 1);
  }

  public MonthPeriod(LocalDate startDate) {
    this.startDate = Objects.requireNonNull(startDate, "startDate must not be null");
    this.month = startDate.getMonthValue();
    this.year = startDate.getYear();
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
