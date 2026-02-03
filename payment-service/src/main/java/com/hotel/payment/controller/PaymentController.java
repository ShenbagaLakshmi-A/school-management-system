package com.hotel.payment.controller;

import com.hotel.payment.dto.PaymentRequest;
import com.hotel.payment.dto.PaymentResponse;
import com.hotel.payment.entity.Payment;
import com.hotel.payment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public PaymentResponse makePayment(@RequestBody PaymentRequest request) {
        Payment payment = service.processPayment(request);

        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());

        return response;
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return service.getAllPayments();
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return service.getPaymentById(id);
    }
}
