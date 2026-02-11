package com.hotel.hotel.service;

import com.hotel.hotel.dto.HotelRequestDTO;
import com.hotel.hotel.dto.HotelResponseDTO;

import java.util.List;

public interface HotelService {

    HotelResponseDTO createHotel(HotelRequestDTO request);

    HotelResponseDTO getHotelById(Long id);

    List<HotelResponseDTO> getAllHotels();

    void allocateRoom(Long hotelId);

    void releaseRoom(Long hotelId);
}

