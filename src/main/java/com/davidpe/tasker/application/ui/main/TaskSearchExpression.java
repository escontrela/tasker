package com.davidpe.tasker.application.ui.main;

import java.util.Objects;
import java.util.regex.Pattern;

/** Compiled, case-insensitive regular expression used by the task workspace search. */
final class TaskSearchExpression {

  private final String source;
  private final Pattern pattern;

  private TaskSearchExpression(String source, Pattern pattern) {
    this.source = source;
    this.pattern = pattern;
  }

  static TaskSearchExpression compile(String source) {
    Objects.requireNonNull(source, "source");
    return new TaskSearchExpression(
        source, Pattern.compile(source, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
  }

  String source() {
    return source;
  }

  boolean matches(Object value) {
    return value != null && pattern.matcher(String.valueOf(value)).find();
  }
}
