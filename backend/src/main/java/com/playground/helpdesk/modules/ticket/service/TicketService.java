package com.playground.helpdesk.modules.ticket.service;

import com.playground.helpdesk.core.exception.ResourceNotFoundException;
import com.playground.helpdesk.modules.ticket.domain.Ticket;
import com.playground.helpdesk.modules.ticket.domain.TicketPriority;
import com.playground.helpdesk.modules.ticket.domain.TicketStatus;
import com.playground.helpdesk.modules.ticket.repository.TicketRepository;
import com.playground.helpdesk.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;

    @Transactional
    public Ticket openTicket(String title, String description, TicketPriority priority, UUID customerId) {
        userService.findById(customerId);

        Ticket ticket = new Ticket(title, description, priority, customerId);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket assignTicket(UUID ticketId, UUID assigneeId) {
        userService.findById(assigneeId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado."));

        ticket.setAssigneeId(assigneeId);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket updateStatus(UUID ticketId, TicketStatus newStatus) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado."));

        ticket.setStatus(newStatus);

        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public Page<Ticket> listAll(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Ticket> listByCustomer(UUID customerId, Pageable pageable) {
        return ticketRepository.findByCustomerId(customerId, pageable);
    }
}
