/**
 * Service responsible for keeping a cache of domain entities in sync with domain events.
 *
 * <p>This component listens for lifecycle events that affect cached entities (for example:
 * creation, update, deletion, bulk changes or other domain events that imply a state change) and
 * updates the cache after the surrounding transaction has committed.
 *
 * <p>Typical responsibilities:
 *
 * <ul>
 *   <li>React to "created" events by loading the current representation (for example, a fresh list
 *       or aggregate) and updating the cache entry for the affected key.
 *   <li>React to "updated" events by refreshing the cached value so callers observe the latest
 *       state.
 *   <li>React to "deleted" events by evicting the corresponding cache entry to avoid serving stale
 *       data.
 *   <li>Handle bulk-change events (for example, when many entities for a user change at once) by
 *       either refreshing the aggregated cache entry or evicting it so it will be repopulated on
 *       the next access.
 * </ul>
 */
package com.davidpe.tasker.application.service.cache;

import com.davidpe.tasker.domain.project.Project;
import com.davidpe.tasker.domain.project.ProjectCreatedEvent;
import com.davidpe.tasker.domain.project.ProjectRepository;
import com.davidpe.tasker.domain.user.User;
import com.davidpe.tasker.domain.user.UserRepository;
import com.davidpe.tasker.domain.user.UserUpdateEvent;
import java.util.List;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class RefreshCacheService {

  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;

  public RefreshCacheService(ProjectRepository projectRepository, UserRepository userRepository) {

    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @CachePut(value = "projects", key = "#p0.entity().getUserId()")
  public List<Project> onProjectCreated(ProjectCreatedEvent event) {

    return projectRepository.findByUserId(event.entity().getUserId());
  }

  @CachePut(value = "users", key = "#p0.entity().getId()")
  public User onUserUpdate(UserUpdateEvent event) {

    return userRepository.save(event.entity());
  }
}
