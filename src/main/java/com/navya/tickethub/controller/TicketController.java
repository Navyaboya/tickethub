package com.navya.tickethub.controller;

import com.navya.tickethub.dto.BookingRequest;
import com.navya.tickethub.model.Ticket;
import com.navya.tickethub.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ticket book(@Valid @RequestBody BookingRequest request) {
        return service.book(request);
    }

    @GetMapping
    public List<Ticket> list(@RequestParam(required = false) String eventName) {
        return service.findAll(eventName);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/{id}")
    public Ticket get(@PathVariable Long id) {
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public Ticket cancel(@PathVariable Long id) {
        return service.cancel(id);
    }
}
