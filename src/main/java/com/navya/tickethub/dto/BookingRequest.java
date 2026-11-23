package com.navya.tickethub.dto;

import jakarta.validation.constraints.NotBlank;

public record BookingRequest(
        @NotBlank(message = "eventName is required") String eventName,
        @NotBlank(message = "seatNumber is required") String seatNumber,
        @NotBlank(message = "customerName is required") String customerName
) {
}
