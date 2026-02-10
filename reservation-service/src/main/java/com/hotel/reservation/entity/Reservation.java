package com.hotel.reservation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private Long hotelId;
    private Long paymentId;

    private String status;
}
