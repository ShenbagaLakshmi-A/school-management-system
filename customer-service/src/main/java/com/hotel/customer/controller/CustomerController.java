package com.hotel.customer.controller;

import com.hotel.customer.dto.CustomerRequestDTO;
import com.hotel.customer.dto.CustomerResponseDTO;
import com.hotel.customer.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    public CustomerResponseDTO createCustomer(@RequestBody CustomerRequestDTO request) {
        return service.createCustomer(request);
    }

    @GetMapping("/{id}")
    public CustomerResponseDTO getCustomer(@PathVariable Long id) {
        return service.getCustomerById(id);
    }

    @GetMapping
    public List<CustomerResponseDTO> getAllCustomers() {
        return service.getAllCustomers();
    }
}
