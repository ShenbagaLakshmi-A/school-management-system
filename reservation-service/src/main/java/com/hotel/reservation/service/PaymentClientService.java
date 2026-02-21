package com.hotel.reservation.service;

import com.hotel.reservation.entity.Reservation;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.HashMap;

@Service
public class PaymentClientService {

    private final RestTemplate restTemplate;

    public PaymentClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // No recordExceptions here; it will be configured in application.yml
    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
	public Map<String, Object> processPayment(Reservation reservation, Double amount) {

		System.out.println(">>> CALLING PAYMENT SERVICE <<<");

		Map<String, Object> paymentRequest = Map.of(
			"customerId", reservation.getCustomerId(),
			"hotelId", reservation.getHotelId(),
			"checkIn", reservation.getCheckIn(),
			"checkOut", reservation.getCheckOut(),
			"amount", amount != null ? amount : 1000.0
		);

		Map<String, Object> response;
		try {
			response = restTemplate.postForObject(
				"http://payment-service:8084/payments",
				paymentRequest,
				Map.class
			);
		} catch (RestClientException ex) {
			throw new RuntimeException("Payment service call failed: " + ex.getMessage(), ex);
		}

		if (response == null || "FAILED".equals(response.get("status"))) {
			throw new RuntimeException("Payment failed for reservation " + reservation.getId());
		}

		return response;
	}

    // Fallback method
    public Map<String, Object> paymentFallback(Reservation reservation,
                                               Double amount,
                                               Throwable ex) {

        System.out.println(">>> PAYMENT FALLBACK TRIGGERED <<<");
        System.out.println("Reason: " + ex.getMessage());

        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("status", "FAILED");
        fallbackResponse.put("reservationId", reservation.getId());
        fallbackResponse.put("message", "Payment Service unavailable (Circuit Open or Error)");

        return fallbackResponse;
    }
}