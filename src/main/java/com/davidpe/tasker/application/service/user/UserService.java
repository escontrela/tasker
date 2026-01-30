package com.davidpe.tasker.application.service.user;

import com.davidpe.tasker.domain.user.User;
import com.davidpe.tasker.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service that exposes the currently selected User for the application.
 *
 * <p>This is a Spring {@code @Service} that is constructed with a {@code UserRepository} and uses a
 * configuration property to determine which user is considered "selected". The selected user id is
 * injected from the environment via {@code @Value("${application.selected-user-id:#{null}}")} and
 * is returned by {@link #getSelectedUser()}.
 *
 * <p>Behavior summary:
 *
 * <ul>
 *   <li>If the {@code application.selected-user-id} property is not configured (or is null), {@link
 *       #getSelectedUser()} throws a {@link RuntimeException} indicating that the selected user id
 *       is not configured.
 *   <li>The method that loads the User by id ({@link #getSelectedUser(Long)}) is annotated with
 *       {@code @Cacheable(value = "users", key = "#selectedUserId")}, so resolved users are cached
 *       in the "users" cache by their id. The cache semantics (TTL, eviction, etc.) are governed by
 *       the cache configuration present in the application.
 *   <li>Actual user retrieval is delegated to the injected {@code UserRepository} via {@code
 *       userRepository.findById(selectedUserId)}.
 * </ul>
 */
@Service
public class UserService {

  private UserRepository userRepository;

  public UserService(UserRepository userRepository) {

    this.userRepository = userRepository;
  }

  @Value("${application.selected-user-id:#{null}}")
  private Long configuredSelectedUserId;

  public User getSelectedUser() {

    Long selectedUserId = getSelectedUserIdFromConfig();

    if (selectedUserId == null) {

      throw new RuntimeException("Selected user id is not configured in application properties");
    }

    return getSelectedUser(selectedUserId);
  }

  @Cacheable(value = "users", key = "#selectedUserId")
  private User getSelectedUser(Long selectedUserId) {

    return userRepository.findById(selectedUserId);
  }

  /**
   * Retrieves the selected user id from application.yml (app.selected-user-id). Returns null if it
   * is not configured.
   */
  private Long getSelectedUserIdFromConfig() {

    return configuredSelectedUserId;
  }
}
