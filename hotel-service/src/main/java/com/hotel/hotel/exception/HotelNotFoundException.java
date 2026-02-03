package com.hotel.hotel.exception;

public class HotelNotFoundException extends RuntimeException {
    public HotelNotFoundException(Long id) {
        super("Hotel not found with id: " + id);
    }
}
