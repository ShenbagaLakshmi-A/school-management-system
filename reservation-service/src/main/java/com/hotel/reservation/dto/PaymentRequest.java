package com.hotel.reservation.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long reservationId;  // Reservation ID
    private Double amount;       // Payment amount
}
