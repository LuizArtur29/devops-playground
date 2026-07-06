package com.playground.helpdesk.modules.ticket.repository;

import com.playground.helpdesk.modules.ticket.domain.Ticket;
import com.playground.helpdesk.modules.ticket.domain.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    Page<Ticket> findByCustomerId(UUID customerId, Pageable pageable);
    Page<Ticket> findByAssigneeId(UUID assigneeId, Pageable pageable);
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

}
