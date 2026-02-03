package com.hotel.hotel.controller;

import com.hotel.hotel.dto.HotelRequestDTO;
import com.hotel.hotel.dto.HotelResponseDTO;
import com.hotel.hotel.service.HotelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService service;

    public HotelController(HotelService service) {
        this.service = service;
    }

    @PostMapping
    public HotelResponseDTO createHotel(@RequestBody HotelRequestDTO request) {
        return service.createHotel(request);
    }

    @GetMapping("/{id}")
    public HotelResponseDTO getHotel(@PathVariable Long id) {
        return service.getHotelById(id);
    }

    @GetMapping
    public List<HotelResponseDTO> getAllHotels() {
        return service.getAllHotels();
    }

    // Called internally by Reservation Service
    @PostMapping("/{id}/allocate")
    public void allocateRoom(@PathVariable Long id) {
        service.allocateRoom(id);
    }

    @PostMapping("/{id}/release")
    public void releaseRoom(@PathVariable Long id) {
        service.releaseRoom(id);
    }
}
