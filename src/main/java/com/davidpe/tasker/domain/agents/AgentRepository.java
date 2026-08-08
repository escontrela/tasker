package com.davidpe.tasker.domain.agents;

import java.util.List;
import java.util.Optional;

public interface AgentRepository {

  List<Agent> findAll();

  Optional<Agent> findById(Long id);

  Optional<Agent> findByCode(String code);

  Agent save(Agent agent);
}
