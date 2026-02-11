package com.hotel.reservation.dto;

import lombok.Data;

@Data
public class ReservationRequest {
    private Long customerId;   // ID of the customer
    private Long hotelId;      // ID of the hotel
    private String checkIn;    // Check-in date (e.g., "2026-02-15")
    private String checkOut;   // Check-out date (e.g., "2026-02-20")
    private Double amount;     // Amount for payment
}
