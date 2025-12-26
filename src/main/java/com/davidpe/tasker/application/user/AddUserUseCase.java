package com.davidpe.tasker.application.user;

import com.davidpe.tasker.domain.user.User;
import com.davidpe.tasker.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddUserUseCase {

    private final UserRepository userRepository;

    public AddUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User addUser(AddUserCommand command) {
        User newUser = User.newUser(command.email());
        return userRepository.save(newUser);
    }
}
