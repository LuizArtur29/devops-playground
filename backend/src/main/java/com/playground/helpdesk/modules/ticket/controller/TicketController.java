package com.playground.helpdesk.modules.ticket.controller;

import com.playground.helpdesk.modules.ticket.domain.Ticket;
import com.playground.helpdesk.modules.ticket.domain.TicketStatus;
import com.playground.helpdesk.modules.ticket.dto.request.TicketCreateRequest;
import com.playground.helpdesk.modules.ticket.dto.response.TicketResponse;
import com.playground.helpdesk.modules.ticket.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> open(@RequestBody @Valid TicketCreateRequest request) {
        Ticket ticket = ticketService.openTicket(
                request.title(),
                request.description(),
                request.priority(),
                request.customerId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(ticket));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> assign(@PathVariable UUID id, @RequestParam UUID assigneeId) {
        Ticket ticket = ticketService.assignTicket(id, assigneeId);
        return ResponseEntity.ok(mapToResponse(ticket));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(@PathVariable UUID id, @RequestParam TicketStatus status) {
        Ticket ticket = ticketService.updateStatus(id, status);
        return ResponseEntity.ok(mapToResponse(ticket));
    }

    @GetMapping
    public ResponseEntity<Page<TicketResponse>> listAll(@PageableDefault(size = 10) Pageable pageable) {
        Page<TicketResponse> tickets = ticketService.listAll(pageable).map(this::mapToResponse);
        return ResponseEntity.ok(tickets);
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCustomerId(),
                ticket.getAssigneeId(),
                ticket.getCreatedAt()
        );
    }
}