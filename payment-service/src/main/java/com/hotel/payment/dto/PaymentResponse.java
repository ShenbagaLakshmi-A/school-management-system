package com.hotel.payment.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private Long id;
    private Double amount;
    private String status;
}
