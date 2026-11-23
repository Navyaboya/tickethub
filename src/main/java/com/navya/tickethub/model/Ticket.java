package com.navya.tickethub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets", indexes = {
        @Index(name = "idx_event_seat", columnList = "eventName, seatNumber")
})
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventName;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Column(nullable = false)
    private LocalDateTime bookedAt;

    // Optimistic locking: concurrent updates to the same ticket are detected
    @Version
    private Long version;

    public Ticket() {
    }

    public Ticket(String eventName, String seatNumber, String customerName) {
        this.eventName = eventName;
        this.seatNumber = seatNumber;
        this.customerName = customerName;
        this.status = TicketStatus.BOOKED;
        this.bookedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getEventName() { return eventName; }
    public String getSeatNumber() { return seatNumber; }
    public String getCustomerName() { return customerName; }
    public TicketStatus getStatus() { return status; }
    public LocalDateTime getBookedAt() { return bookedAt; }
    public Long getVersion() { return version; }

    public void setId(Long id) { this.id = id; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public void setBookedAt(LocalDateTime bookedAt) { this.bookedAt = bookedAt; }
    public void setVersion(Long version) { this.version = version; }
}
