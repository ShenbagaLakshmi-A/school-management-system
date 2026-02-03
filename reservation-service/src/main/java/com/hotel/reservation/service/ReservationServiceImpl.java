package com.hotel.reservation.service;

import com.hotel.reservation.dto.CustomerDTO;
import com.hotel.reservation.dto.PaymentDTO;
import com.hotel.reservation.dto.PaymentRequest;
import com.hotel.reservation.entity.Reservation;
import com.hotel.reservation.exception.ExternalServiceException;
import com.hotel.reservation.exception.ReservationException;
import com.hotel.reservation.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository repository;
    private final RestTemplate restTemplate;

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    @Value("${hotel.service.url}")
    private String hotelServiceUrl;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    public ReservationServiceImpl(ReservationRepository repository,
                                  RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Reservation createReservation(Reservation reservation) {

        validateInput(reservation);

        // 1️⃣ Validate Customer
        validateCustomer(reservation.getCustomerId());

        // 2️⃣ Validate & Allocate Hotel Room
        allocateHotelRoom(reservation.getHotelId());

        try {
            // 3️⃣ Save Reservation
            reservation.setStatus("CREATED");
            Reservation saved = repository.save(reservation);

            // 4️⃣ Call Payment Service
            processPayment(saved);

            saved.setStatus("CONFIRMED");
            return repository.save(saved);

        } catch (Exception ex) {
            // 5️⃣ Rollback hotel room on failure
            rollbackHotelRoom(reservation.getHotelId());
            throw ex;
        }
    }

    private void validateInput(Reservation reservation) {
        if (reservation.getCustomerId() == null ||
            reservation.getHotelId() == null ||
            reservation.getCheckInDate() == null ||
            reservation.getCheckOutDate() == null) {
            throw new ReservationException("Invalid reservation data");
        }
    }

    private void validateCustomer(Long customerId) {
        try {
            restTemplate.getForObject(
                    customerServiceUrl + "/customers/" + customerId,
                    CustomerDTO.class
            );
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Customer service not reachable");
        }
    }

    private void allocateHotelRoom(Long hotelId) {
        try {
            restTemplate.postForObject(
                    hotelServiceUrl + "/hotels/" + hotelId + "/allocate",
                    null,
                    Void.class
            );
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Hotel service failed to allocate room");
        }
    }

    private void rollbackHotelRoom(Long hotelId) {
        try {
            restTemplate.postForObject(
                    hotelServiceUrl + "/hotels/" + hotelId + "/release",
                    null,
                    Void.class
            );
        } catch (Exception ex) {
            log.error("Rollback failed for hotel {}", hotelId);
        }
    }

    private void processPayment(Reservation reservation) {
        PaymentRequest request = new PaymentRequest();
        request.setReservationId(reservation.getId());
        request.setAmount(reservation.getAmount());

        try {
            PaymentDTO response = restTemplate.postForObject(
                    paymentServiceUrl + "/payments",
                    request,
                    PaymentDTO.class
            );

            if (response == null || !"SUCCESS".equalsIgnoreCase(response.getStatus())) {
                throw new ReservationException("Payment failed");
            }
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Payment service unavailable");
        }
    }

    @Override
    public List<Reservation> getAllReservations() {
        return repository.findAll();
    }

    @Override
    public Reservation getReservationById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ReservationException("Reservation not found"));
    }
}
