package com.davidpe.tasker.application.task;

import java.time.LocalDate;

public record UpdateTaskCommand(Long taskId,
                                Long projectId,
                                Long priorityId,
                                Long tagId,
                                String externalCode,
                                String title,
                                String description,
                                LocalDate startDate,
                                LocalDate endDate) {
}
