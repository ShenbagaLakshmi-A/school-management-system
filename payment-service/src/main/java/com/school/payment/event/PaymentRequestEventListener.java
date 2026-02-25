package com.school.payment.event;

import com.school.payment.entity.Payment;
import com.school.payment.service.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentRequestEventListener {

    private final PaymentService paymentService;
    private final PaymentCompletedEventPublisher completedPublisher;

    public PaymentRequestEventListener(PaymentService paymentService,
                                       PaymentCompletedEventPublisher completedPublisher) {
        this.paymentService = paymentService;
        this.completedPublisher = completedPublisher;
    }

    @RabbitListener(queues = "payment.queue")
    public void handlePaymentRequest(PaymentRequestEvent event) {

        System.out.println("Received PaymentRequestEvent for feeId: " + event.getFeeId());

        // Convert Event → Payment Entity
        Payment payment = new Payment();
        payment.setFeeId(event.getFeeId());
        payment.setStudentId(event.getStudentId());
        payment.setAmount(event.getAmount());

        // Process payment
        Payment processedPayment = paymentService.processPayment(payment);

        // Publish completion event
        completedPublisher.publishPaymentCompleted(processedPayment);
    }
}