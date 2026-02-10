package com.hotel.reservation.service;

import com.hotel.reservation.client.PaymentClient;
import com.hotel.reservation.dto.*;
import com.hotel.reservation.entity.Reservation;
import com.hotel.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private final ReservationRepository repository;
    private final PaymentClient paymentClient;

    public ReservationService(ReservationRepository repository,
                              PaymentClient paymentClient) {
        this.repository = repository;
        this.paymentClient = paymentClient;
    }

    public ReservationResponse createReservation(ReservationRequest request) {

        // 1️⃣ Save reservation first
        Reservation reservation = new Reservation();
        reservation.setCustomerId(request.getCustomerId());
        reservation.setHotelId(request.getHotelId());
        reservation.setStatus("CREATED");

        reservation = repository.save(reservation);

        // 2️⃣ Call payment service
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setReservationId(reservation.getId());
        paymentRequest.setAmount(request.getAmount());

        PaymentResponse paymentResponse =
                paymentClient.processPayment(paymentRequest);

        // 3️⃣ Update reservation
        reservation.setPaymentId(paymentResponse.getId());
        reservation.setStatus("CONFIRMED");
        repository.save(reservation);

        // 4️⃣ Response
        ReservationResponse response = new ReservationResponse();
        response.setReservationId(reservation.getId());
        response.setReservationStatus(reservation.getStatus());
        response.setPaymentStatus(paymentResponse.getStatus());

        return response;
    }
}
