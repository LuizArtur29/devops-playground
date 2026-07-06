package com.playground.helpdesk.modules.ticket.dto.response;

import com.playground.helpdesk.modules.ticket.domain.TicketPriority;
import com.playground.helpdesk.modules.ticket.domain.TicketStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TicketResponse(
         UUID id,
         String title,
         String description,
         TicketStatus status,
         TicketPriority priority,
         UUID customerId,
         UUID assigneeId,
         OffsetDateTime createdAt
) {
}
