package com.davidpe.tasker.application.ui.main;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.Test;

class TaskSearchExpressionTest {

  @Test
  void matchesRegularExpressionInsideTextIgnoringCase() {
    TaskSearchExpression expression = TaskSearchExpression.compile("tree-[0-9]{4}");

    assertTrue(expression.matches("TREE-0175 — Viewer toolbar"));
    assertFalse(expression.matches("TASK-0175 — Viewer toolbar"));
  }

  @Test
  void matchesNumericAndTextValues() {
    TaskSearchExpression expression = TaskSearchExpression.compile("^(42|done)$");

    assertTrue(expression.matches(42L));
    assertTrue(expression.matches("DONE"));
    assertFalse(expression.matches("planned"));
  }

  @Test
  void doesNotMatchNullValues() {
    assertFalse(TaskSearchExpression.compile(".*").matches(null));
  }

  @Test
  void rejectsInvalidRegularExpressions() {
    assertThrows(PatternSyntaxException.class, () -> TaskSearchExpression.compile("[missing"));
  }
}
