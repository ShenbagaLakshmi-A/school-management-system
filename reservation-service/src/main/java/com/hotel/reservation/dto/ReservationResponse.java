package com.hotel.reservation.dto;

import lombok.Data;

@Data
public class ReservationResponse {
    private Long reservationId;   // Reservation ID
    private Long customerId;      // Customer ID
    private Long hotelId;         // Hotel ID
    private String checkIn;       // Check-in date
    private String checkOut;      // Check-out date
    private String status;        // Reservation status: CREATED / CONFIRMED / FAILED
    private Long paymentId;       // Payment ID (assigned after payment)
    private Long notificationId;  // Notification ID (assigned after sending notification)
}
