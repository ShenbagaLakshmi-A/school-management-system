package com.hotel.reservation.dto;

import lombok.Data;

@Data
public class ReservationRequest {
    private Long customerId;
    private Long hotelId;
    private Double amount;
}
