package com.school.payment.controller;

import com.school.payment.entity.Payment;
import com.school.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Get all payments
    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    // Process payment (called by Fee Service)
    @PostMapping("/process")
    public Map<String, Object> processPayment(@RequestBody Payment payment) {
        Payment savedPayment = paymentService.processPayment(payment);

        // Return JSON with 'status' and 'id'
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedPayment.getId());
        response.put("status", savedPayment.getStatus().name());

        return response;
    }
}