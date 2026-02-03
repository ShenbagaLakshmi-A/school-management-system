package com.hotel.reservation.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long reservationId;
    private Double amount;
}
