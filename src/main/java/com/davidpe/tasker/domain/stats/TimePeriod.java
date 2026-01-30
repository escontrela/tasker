package com.davidpe.tasker.domain.stats;

import java.time.LocalDate;

public interface TimePeriod {

  String getLabel();

  LocalDate getStartDate();
}
