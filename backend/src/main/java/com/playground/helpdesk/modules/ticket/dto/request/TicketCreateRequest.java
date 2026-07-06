package com.playground.helpdesk.modules.ticket.dto.request;

import com.playground.helpdesk.modules.ticket.domain.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TicketCreateRequest(
        @NotBlank(message = "O título é obrigatório.")
        String title,

        @NotBlank(message = "A descrição é obrigatória.")
        String description,

        @NotNull(message = "A prioridade é obrigatória.")
        TicketPriority priority,

        @NotNull(message = "O ID do cliente é obrigatório.")
        UUID customerId
) {
}