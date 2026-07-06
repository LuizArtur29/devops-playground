package com.playground.helpdesk.modules.user.dto.response;

import com.playground.helpdesk.modules.user.domain.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role
) {
}