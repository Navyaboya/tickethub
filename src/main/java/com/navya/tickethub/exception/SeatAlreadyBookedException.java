package com.navya.tickethub.exception;

public class SeatAlreadyBookedException extends RuntimeException {
    public SeatAlreadyBookedException(String eventName, String seatNumber) {
        super("Seat " + seatNumber + " is already booked for event: " + eventName);
    }
}
