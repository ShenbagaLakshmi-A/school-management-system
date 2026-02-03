package com.hotel.reservation.service;

import com.hotel.reservation.entity.Reservation;

import java.util.List;

public interface ReservationService {

    Reservation createReservation(Reservation reservation);

    List<Reservation> getAllReservations();

    Reservation getReservationById(Long id);
}
