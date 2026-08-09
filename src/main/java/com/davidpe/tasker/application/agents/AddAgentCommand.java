package com.davidpe.tasker.application.agents;

public record AddAgentCommand(String code, String name, Long roleId) {}
