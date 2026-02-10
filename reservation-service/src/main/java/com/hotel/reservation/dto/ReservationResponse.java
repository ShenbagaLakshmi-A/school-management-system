package com.hotel.reservation.dto;

import lombok.Data;

@Data
public class ReservationResponse {
    private Long reservationId;
    private String reservationStatus;
    private String paymentStatus;
}
