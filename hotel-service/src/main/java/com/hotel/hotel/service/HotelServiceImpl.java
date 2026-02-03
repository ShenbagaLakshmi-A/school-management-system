package com.hotel.hotel.service;

import com.hotel.hotel.dto.HotelRequestDTO;
import com.hotel.hotel.dto.HotelResponseDTO;
import com.hotel.hotel.entity.Hotel;
import com.hotel.hotel.exception.HotelNotFoundException;
import com.hotel.hotel.exception.InvalidHotelException;
import com.hotel.hotel.exception.NoRoomsAvailableException;
import com.hotel.hotel.repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository repository;

    public HotelServiceImpl(HotelRepository repository) {
        this.repository = repository;
    }

    @Override
    public HotelResponseDTO createHotel(HotelRequestDTO request) {

        if (request.getName() == null || request.getTotalRooms() == null) {
            throw new InvalidHotelException("Hotel name and total rooms are mandatory");
        }

        Hotel hotel = new Hotel(
                null,
                request.getName(),
                request.getCity(),
                request.getAddress(),
                request.getTotalRooms(),
                request.getTotalRooms()
        );

        Hotel saved = repository.save(hotel);

        return new HotelResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getCity(),
                saved.getAvailableRooms()
        );
    }

    @Override
    public HotelResponseDTO getHotelById(Long id) {

        Hotel hotel = repository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));

        return new HotelResponseDTO(
                hotel.getId(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getAvailableRooms()
        );
    }

    @Override
    public List<HotelResponseDTO> getAllHotels() {
        return repository.findAll()
                .stream()
                .map(h -> new HotelResponseDTO(
                        h.getId(),
                        h.getName(),
                        h.getCity(),
                        h.getAvailableRooms()
                ))
                .toList();
    }

    /**
     * Called by Reservation Service
     */
    @Transactional
    @Override
    public void allocateRoom(Long hotelId) {

        Hotel hotel = repository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));

        if (hotel.getAvailableRooms() <= 0) {
            throw new NoRoomsAvailableException(hotelId);
        }

        hotel.setAvailableRooms(hotel.getAvailableRooms() - 1);
        repository.save(hotel);
    }

    /**
     * Used for cancellation / rollback
     */
    @Transactional
    @Override
    public void releaseRoom(Long hotelId) {

        Hotel hotel = repository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));

        hotel.setAvailableRooms(hotel.getAvailableRooms() + 1);
        repository.save(hotel);
    }
}
