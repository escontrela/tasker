package com.davidpe.tasker.api.user;

import com.davidpe.tasker.application.user.AddUserCommand;
import com.davidpe.tasker.application.user.AddUserUseCase;
import com.davidpe.tasker.domain.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class AddUserController {

    private final AddUserUseCase addUserUseCase;

    public AddUserController(AddUserUseCase addUserUseCase) {
        this.addUserUseCase = addUserUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddUserResponse addUser(@Valid @RequestBody AddUserRequest request) {
        User created = addUserUseCase.addUser(new AddUserCommand(request.email()));
        return new AddUserResponse(created.getId(), created.getEmail(), created.getCreatedAt());
    }
}
