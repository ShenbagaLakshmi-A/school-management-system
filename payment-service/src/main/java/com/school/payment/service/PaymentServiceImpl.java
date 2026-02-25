package com.school.payment.service;

import com.school.payment.entity.Payment;
import com.school.payment.entity.PaymentStatus;
import com.school.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
	public Payment processPayment(Payment payment) {
		payment.setStatus(PaymentStatus.SUCCESS); // use your enum
		payment.setPaymentDate(LocalDateTime.now());
		return paymentRepository.save(payment);
	}
}