package com.davidpe.tasker.application.task;

/** Assigns an agent to a task; a {@code null} agent id clears the assignment. */
public record AssignTaskAgentCommand(Long taskId, Long agentId) {}
