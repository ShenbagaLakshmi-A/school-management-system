package com.school.fee.event;

import com.school.fee.entity.Fee;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentRequestEventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String PAYMENT_EXCHANGE = "payment.exchange";
    private static final String PAYMENT_ROUTING_KEY = "payment.request";

    public void publishPaymentRequest(Fee fee) {
        rabbitTemplate.convertAndSend(PAYMENT_EXCHANGE, PAYMENT_ROUTING_KEY, fee);
        System.out.println("PaymentRequestEvent published for feeId: " + fee.getId());
    }
}