package com.navya.tickethub.service;

import com.navya.tickethub.dto.BookingRequest;
import com.navya.tickethub.exception.SeatAlreadyBookedException;
import com.navya.tickethub.exception.TicketNotFoundException;
import com.navya.tickethub.model.Ticket;
import com.navya.tickethub.model.TicketStatus;
import com.navya.tickethub.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository repository;

    @InjectMocks
    private TicketService service;

    @Test
    void bookSavesTicketWhenSeatIsFree() {
        BookingRequest request = new BookingRequest("Concert", "A1", "Navya");
        when(repository.existsByEventNameAndSeatNumberAndStatus("Concert", "A1", TicketStatus.BOOKED))
                .thenReturn(false);
        when(repository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = service.book(request);

        assertEquals("Concert", result.getEventName());
        assertEquals("A1", result.getSeatNumber());
        assertEquals(TicketStatus.BOOKED, result.getStatus());
        verify(repository).save(any(Ticket.class));
    }

    @Test
    void bookThrowsWhenSeatAlreadyBooked() {
        BookingRequest request = new BookingRequest("Concert", "A1", "Navya");
        when(repository.existsByEventNameAndSeatNumberAndStatus("Concert", "A1", TicketStatus.BOOKED))
                .thenReturn(true);

        assertThrows(SeatAlreadyBookedException.class, () -> service.book(request));
        verify(repository, never()).save(any());
    }

    @Test
    void cancelMarksTicketCancelled() {
        Ticket ticket = new Ticket("Concert", "A1", "Navya");
        when(repository.findById(1L)).thenReturn(Optional.of(ticket));
        when(repository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = service.cancel(1L);

        assertEquals(TicketStatus.CANCELLED, result.getStatus());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> service.findById(99L));
    }
}
