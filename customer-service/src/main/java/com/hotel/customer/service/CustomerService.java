package com.hotel.customer.service;

import com.hotel.customer.dto.CustomerRequestDTO;
import com.hotel.customer.dto.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO createCustomer(CustomerRequestDTO request);
    CustomerResponseDTO getCustomerById(Long id);
    List<CustomerResponseDTO> getAllCustomers();
}
