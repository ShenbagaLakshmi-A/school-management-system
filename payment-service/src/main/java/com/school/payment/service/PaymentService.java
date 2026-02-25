package com.school.payment.service;

import com.school.payment.entity.Payment;

import java.util.List;

public interface PaymentService {

    List<Payment> getAllPayments();

    Payment processPayment(Payment payment);
}