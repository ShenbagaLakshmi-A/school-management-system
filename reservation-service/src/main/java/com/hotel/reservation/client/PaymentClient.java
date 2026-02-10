package com.hotel.reservation.client;

import com.hotel.reservation.dto.PaymentRequest;
import com.hotel.reservation.dto.PaymentResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public PaymentResponse processPayment(PaymentRequest request) {
        return restTemplate.postForObject(
                "http://payment-service:8084/payments",
                request,
                PaymentResponse.class
        );
    }
}
