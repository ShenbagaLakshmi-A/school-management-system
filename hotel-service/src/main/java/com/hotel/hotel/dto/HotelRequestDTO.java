package com.hotel.hotel.dto;

import lombok.Data;

@Data
public class HotelRequestDTO {
    private String name;
    private String city;
    private String address;
    private Integer totalRooms;
}
