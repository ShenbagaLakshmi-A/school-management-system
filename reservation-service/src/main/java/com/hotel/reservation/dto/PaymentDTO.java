package com.hotel.reservation.dto;

import lombok.Data;

@Data
public class PaymentDTO {
    private Long id;
    private Double amount;
    private String status;
}
