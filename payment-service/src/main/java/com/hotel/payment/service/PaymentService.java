package com.hotel.payment.service;

import com.hotel.payment.dto.PaymentRequest;
import com.hotel.payment.entity.Payment;

import java.util.List;

public interface PaymentService {

    Payment processPayment(PaymentRequest request);

    List<Payment> getAllPayments();

    Payment getPaymentById(Long id);
}
