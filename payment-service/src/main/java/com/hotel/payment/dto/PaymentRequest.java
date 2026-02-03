package com.hotel.payment.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long reservationId;
    private Double amount;
}
