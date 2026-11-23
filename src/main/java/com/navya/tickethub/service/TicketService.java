package com.navya.tickethub.service;

import com.navya.tickethub.dto.BookingRequest;
import com.navya.tickethub.exception.SeatAlreadyBookedException;
import com.navya.tickethub.exception.TicketNotFoundException;
import com.navya.tickethub.model.Ticket;
import com.navya.tickethub.model.TicketStatus;
import com.navya.tickethub.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository repository;

    public TicketService(TicketRepository repository) {
        this.repository = repository;
    }

    /**
     * Books a seat. The check-and-save runs in one transaction so a seat that is
     * already BOOKED cannot be booked again (double-booking prevention).
     */
    @Transactional
    public Ticket book(BookingRequest request) {
        boolean taken = repository.existsByEventNameAndSeatNumberAndStatus(
                request.eventName(), request.seatNumber(), TicketStatus.BOOKED);
        if (taken) {
            throw new SeatAlreadyBookedException(request.eventName(), request.seatNumber());
        }
        Ticket ticket = new Ticket(request.eventName(), request.seatNumber(), request.customerName());
        return repository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<Ticket> findAll(String eventName) {
        if (eventName == null || eventName.isBlank()) {
            return repository.findAll();
        }
        return repository.findByEventName(eventName);
    }

    @Transactional(readOnly = true)
    public Ticket findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new TicketNotFoundException(id));
    }

    /**
     * Cancels a ticket (soft cancel: status becomes CANCELLED so the seat can be rebooked).
     * The @Version field on Ticket protects against concurrent updates.
     */
    @Transactional
    public Ticket cancel(Long id) {
        Ticket ticket = findById(id);
        ticket.setStatus(TicketStatus.CANCELLED);
        return repository.save(ticket);
    }
}
