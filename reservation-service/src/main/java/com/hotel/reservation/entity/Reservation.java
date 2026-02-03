package com.hotel.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private Long hotelId;

    private String roomType;
    private String checkInDate;
    private String checkOutDate;

    private String status; // CREATED, CONFIRMED, FAILED

    private Double amount;
}
