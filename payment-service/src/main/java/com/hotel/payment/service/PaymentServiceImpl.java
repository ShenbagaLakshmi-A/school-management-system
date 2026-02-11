package com.hotel.payment.service;

import com.hotel.payment.dto.PaymentRequest;
import com.hotel.payment.dto.PaymentResponse;
import com.hotel.payment.entity.Payment;
import com.hotel.payment.repository.PaymentRepository;

import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    public PaymentServiceImpl(PaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        Payment payment = new Payment();
        payment.setReservationId(request.getReservationId());
        payment.setAmount(request.getAmount());
        payment.setStatus("SUCCESS"); // deterministic for demo

        Payment saved = repository.save(payment);

        return new PaymentResponse(
                saved.getId(),
                saved.getReservationId(),
                saved.getStatus()
        );
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {

        Payment payment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return new PaymentResponse(
                payment.getId(),
                payment.getReservationId(),
                payment.getStatus()
        );
    }
}
