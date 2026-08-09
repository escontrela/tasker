package com.davidpe.tasker.domain.agents;

import java.util.List;
import java.util.Optional;

public interface AgentRoleRepository {

  List<AgentRole> findAll();

  Optional<AgentRole> findById(Long id);

  Optional<AgentRole> findByCode(String code);

  AgentRole save(AgentRole role);
}
