package com.hotel.hotel.exception;

public class NoRoomsAvailableException extends RuntimeException {
    public NoRoomsAvailableException(Long id) {
        super("No rooms available for hotel id: " + id);
    }
}
