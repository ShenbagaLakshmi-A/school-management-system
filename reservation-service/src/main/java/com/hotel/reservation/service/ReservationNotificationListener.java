package com.hotel.reservation.service;

import com.hotel.reservation.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReservationNotificationListener {

    // Listen to the queue defined in RabbitConfig
    @RabbitListener(queues = RabbitConfig.RESERVATION_QUEUE)
    public void receiveMessage(Map<String, Object> message) {
        System.out.println("✅ Received RabbitMQ message: " + message);
    }
}