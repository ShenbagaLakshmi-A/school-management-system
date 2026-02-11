package com.hotel.reservation.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private Long id;         // Payment ID returned by payment service
    private String status;   // Payment status: SUCCESS / FAILED / INITIATED
}
