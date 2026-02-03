package com.hotel.customer.service;

import com.hotel.customer.dto.CustomerRequestDTO;
import com.hotel.customer.dto.CustomerResponseDTO;
import com.hotel.customer.entity.Customer;
import com.hotel.customer.exception.CustomerNotFoundException;
import com.hotel.customer.exception.DuplicateCustomerException;
import com.hotel.customer.exception.InvalidCustomerException;
import com.hotel.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    public CustomerServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO request) {

        if (request.getName() == null || request.getEmail() == null || request.getPhone() == null) {
            throw new InvalidCustomerException("Name, email and phone are mandatory");
        }

        repository.findByEmail(request.getEmail())
                .ifPresent(c -> {
                    throw new DuplicateCustomerException("Email already exists");
                });

        repository.findByPhone(request.getPhone())
                .ifPresent(c -> {
                    throw new DuplicateCustomerException("Phone already exists");
                });

        Customer customer = new Customer(
                null,
                request.getName(),
                request.getEmail(),
                request.getPhone()
        );

        Customer saved = repository.save(customer);

        return new CustomerResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getPhone()
        );
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {

        Customer customer = repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return repository.findAll()
                .stream()
                .map(c -> new CustomerResponseDTO(
                        c.getId(),
                        c.getName(),
                        c.getEmail(),
                        c.getPhone()
                ))
                .toList();
    }
}
