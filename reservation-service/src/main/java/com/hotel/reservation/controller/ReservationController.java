package com.hotel.reservation.controller;

import com.hotel.reservation.entity.Reservation;
import com.hotel.reservation.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping
    public Reservation create(@RequestBody Reservation reservation) {
        return service.createReservation(reservation);
    }

    @GetMapping
    public List<Reservation> getAll() {
        return service.getAllReservations();
    }

    @GetMapping("/{id}")
    public Reservation getById(@PathVariable Long id) {
        return service.getReservationById(id);
    }
}
