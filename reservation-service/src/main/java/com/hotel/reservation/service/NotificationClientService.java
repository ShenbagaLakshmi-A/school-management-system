package com.hotel.reservation.service;

import com.hotel.reservation.entity.Reservation;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.hotel.reservation.config.RabbitConfig.RESERVATION_EXCHANGE;
import static com.hotel.reservation.config.RabbitConfig.RESERVATION_ROUTING_KEY;

@Service
public class NotificationClientService {

    private final RabbitTemplate rabbitTemplate;

    // Explicitly use the JSON-configured RabbitTemplate
    public NotificationClientService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        System.out.println("Injected RabbitTemplate class: " + rabbitTemplate.getClass());
    }

    @CircuitBreaker(name = "notificationService", fallbackMethod = "notificationFallback")
    public void sendNotification(Reservation reservation, String message) {
        Map<String, Object> notificationMessage = Map.of(
                "reservationId", reservation.getId(),
                "status", reservation.getStatus(),
                "customerId", reservation.getCustomerId(),
                "message", message
        );

        // Publish asynchronously to RabbitMQ using JSON converter
        rabbitTemplate.convertAndSend(RESERVATION_EXCHANGE, RESERVATION_ROUTING_KEY, notificationMessage);
    }

    public void notificationFallback(Reservation reservation, String message, Exception ex) {
        System.err.println("Notification Service is down or RabbitMQ unavailable for reservation ID: " 
                + reservation.getId() + " | Exception: " + ex.getMessage());
    }
}