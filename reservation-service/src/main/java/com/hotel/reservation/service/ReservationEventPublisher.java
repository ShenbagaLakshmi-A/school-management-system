package com.hotel.reservation.service;

import com.hotel.reservation.config.RabbitConfig;
import com.hotel.reservation.entity.Reservation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReservationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ReservationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publish reservation event to RabbitMQ
     * @param reservation Reservation entity
     */
    public void publishReservationEvent(Reservation reservation) {
        Map<String, Object> event = new HashMap<>();
        event.put("reservationId", reservation.getId());
        event.put("customerId", reservation.getCustomerId());
        event.put("hotelId", reservation.getHotelId());
        event.put("status", reservation.getStatus());
        event.put("checkIn", reservation.getCheckIn());
        event.put("checkOut", reservation.getCheckOut());

        // Send to exchange with routing key
        rabbitTemplate.convertAndSend(
                RabbitConfig.RESERVATION_EXCHANGE,
                RabbitConfig.RESERVATION_ROUTING_KEY,
                event
        );

        System.out.println("Published reservation event: " + event);
    }
}