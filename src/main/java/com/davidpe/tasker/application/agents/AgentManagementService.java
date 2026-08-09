package com.davidpe.tasker.application.agents;

import com.davidpe.tasker.domain.agents.Agent;
import com.davidpe.tasker.domain.agents.AgentRepository;
import com.davidpe.tasker.domain.agents.AgentRole;
import com.davidpe.tasker.domain.agents.AgentRoleRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Handles the first iteration of the workspace agent directory. */
@Service
public class AgentManagementService {

  private final AgentRepository agentRepository;
  private final AgentRoleRepository agentRoleRepository;

  public AgentManagementService(
      AgentRepository agentRepository, AgentRoleRepository agentRoleRepository) {
    this.agentRepository = agentRepository;
    this.agentRoleRepository = agentRoleRepository;
  }

  public List<Agent> getAgents() {
    return agentRepository.findAll();
  }

  public List<AgentRole> getRoles() {
    return agentRoleRepository.findAll();
  }

  @Transactional
  public Agent addAgent(AddAgentCommand command) {
    String code = normalizeCode(command.code());
    String name = requireName(command.name(), "Agent name");
    if (command.roleId() == null) {
      throw new IllegalArgumentException("Agent role is required");
    }
    if (agentRepository.findByCode(code).isPresent()) {
      throw new IllegalArgumentException("An agent with this code already exists");
    }
    AgentRole role =
        agentRoleRepository
            .findById(command.roleId())
            .orElseThrow(() -> new IllegalArgumentException("Selected agent role no longer exists"));
    return agentRepository.save(new Agent(null, code, name, role, Instant.now()));
  }

  @Transactional
  public AgentRole addRole(AddAgentRoleCommand command) {
    String code = normalizeCode(command.code());
    String name = requireName(command.name(), "Role name");
    if (agentRoleRepository.findByCode(code).isPresent()) {
      throw new IllegalArgumentException("A role with this code already exists");
    }
    return agentRoleRepository.save(new AgentRole(null, code, name, Instant.now()));
  }

  private String normalizeCode(String code) {
    String normalized = requireName(code, "Code").toUpperCase();
    if (!normalized.matches("[A-Z0-9_-]+")) {
      throw new IllegalArgumentException("Code may only contain letters, numbers, hyphens and underscores");
    }
    return normalized;
  }

  private String requireName(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " is required");
    }
    return value.trim();
  }
}
