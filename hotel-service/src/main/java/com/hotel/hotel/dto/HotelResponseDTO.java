package com.hotel.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HotelResponseDTO {
    private Long id;
    private String name;
    private String city;
    private Integer availableRooms;
}
