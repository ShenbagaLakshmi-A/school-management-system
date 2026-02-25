package com.school.fee.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentClientService {

    private final RestTemplate restTemplate;

    public PaymentClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public Map<String, Object> processPayment(Long reservationId, Object amount) {

        return restTemplate.postForObject(
                "http://payment-service/api/payments/process",
                Map.of(
                        "reservationId", reservationId,
                        "amount", amount
                ),
                Map.class
        );
    }

    public Map<String, Object> paymentFallback(Long reservationId, Object amount, Exception ex) {

        System.out.println("🔥 FALLBACK EXECUTED 🔥");

        Map<String, Object> response = new HashMap<>();
        response.put("status", "FAILED");
        response.put("message", "Payment Service is currently unavailable.");
        response.put("fallback", true);

        return response;
    }
}