package com.hotel.notification.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationListenerService {

    // Must match reservation-service queue name
    private static final String RESERVATION_QUEUE = "notification-queue";

    /**
     * Listens for reservation events from RabbitMQ and sends notifications asynchronously.
     *
     * @param event Map containing reservation details
     */
    @RabbitListener(queues = RESERVATION_QUEUE)
    public void handleReservationEvent(Map<String, Object> event) {
        try {
            Long reservationId = Long.valueOf(String.valueOf(event.get("reservationId")));
            String status = (String) event.get("status");
            Long customerId = Long.valueOf(String.valueOf(event.get("customerId")));
            String message = (String) event.get("message");

            // Example: print or send email/SMS
            System.out.println("Sending notification to customerId: " + customerId
                    + " for reservationId: " + reservationId
                    + " with status: " + status
                    + " | message: " + message);

        } catch (Exception e) {
            System.err.println("Failed to process reservation event: " + event);
            e.printStackTrace();
        }
    }
}