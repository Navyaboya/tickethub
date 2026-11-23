package com.navya.tickethub.repository;

import com.navya.tickethub.model.Ticket;
import com.navya.tickethub.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByEventNameAndSeatNumberAndStatus(String eventName, String seatNumber, TicketStatus status);

    List<Ticket> findByEventName(String eventName);

    List<Ticket> findByStatus(TicketStatus status);
}
