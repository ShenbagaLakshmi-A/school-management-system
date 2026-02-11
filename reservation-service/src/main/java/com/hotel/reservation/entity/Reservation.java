package com.hotel.reservation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;   // ID of the customer making the reservation
    private Long hotelId;      // ID of the hotel being reserved
    private String checkIn;    // check-in date as String, e.g., "2026-02-15"
    private String checkOut;   // check-out date as String, e.g., "2026-02-20"
    private String status;     // Reservation status: CREATED / CONFIRMED / FAILED
    private Long paymentId;    // ID of the payment, assigned after payment confirmation
}
