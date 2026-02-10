package com.hotel.payment.service;

import com.hotel.payment.dto.PaymentRequest;
import com.hotel.payment.entity.Payment;
import com.hotel.payment.exception.PaymentException;
import com.hotel.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    public PaymentServiceImpl(PaymentRepository repository) {
        this.repository = repository;
    }

    @Override
	public PaymentResponse processPayment(PaymentRequest request) {

		if (request.getReservationId() == null || request.getAmount() == null) {
			throw new PaymentException("Invalid payment request");
		}

		if (request.getAmount() <= 0) {
			throw new PaymentException("Payment amount must be greater than zero");
		}

		Payment payment = new Payment();
		payment.setReservationId(request.getReservationId());
		payment.setAmount(request.getAmount());
		payment.setStatus("SUCCESS");

		Payment saved = repository.save(payment);

		// Map to DTO
		PaymentResponse response = new PaymentResponse();
		response.setId(saved.getId());
		response.setAmount(saved.getAmount());
		response.setStatus(saved.getStatus());

		return response;
	}


    @Override
    public List<Payment> getAllPayments() {
        return repository.findAll();
    }

    @Override
    public Payment getPaymentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PaymentException("Payment not found"));
    }
}
