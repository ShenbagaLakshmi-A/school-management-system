package com.hotel.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
}
