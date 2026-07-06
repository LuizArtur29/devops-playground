package com.playground.helpdesk.modules.user.controller;

import com.playground.helpdesk.modules.user.domain.User;
import com.playground.helpdesk.modules.user.dto.request.UserCreateRequest;
import com.playground.helpdesk.modules.user.dto.response.UserResponse;
import com.playground.helpdesk.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UserCreateRequest request) {
        User userEntity = new User(request.name(), request.email(), request.password(), request.role());
        User savedUser = userService.createUser(userEntity);

        UserResponse response = new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
