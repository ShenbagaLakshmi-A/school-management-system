package com.school.payment.event;

import com.school.payment.entity.Payment;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletedEventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String PAYMENT_COMPLETED_EXCHANGE = "payment.completed.exchange";
    private static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed";

    public void publishPaymentCompleted(Payment payment) {
        rabbitTemplate.convertAndSend(
                PAYMENT_COMPLETED_EXCHANGE,
                PAYMENT_COMPLETED_ROUTING_KEY,
                payment
        );
        System.out.println("Published PaymentCompletedEvent for feeId: " + payment.getFeeId());
    }
}