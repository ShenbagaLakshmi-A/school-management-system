package com.school.fee.event;

import com.school.fee.dto.FeeRequest;
import com.school.fee.service.FeeService;
import com.school.fee.event.StudentCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StudentCreatedEventListener {

    private final FeeService feeService;

    public StudentCreatedEventListener(FeeService feeService) {
        this.feeService = feeService;
    }

    @RabbitListener(queues = "student.queue")
    public void handleStudentCreated(StudentCreatedEvent event) {

        System.out.println("Received StudentCreatedEvent for studentId: "
                + event.getStudentId());

        // Default fee amount
        FeeRequest request = new FeeRequest();
        request.setStudentId(event.getStudentId());
        request.setAmount(BigDecimal.valueOf(1000.0));

        try {
            feeService.createFee(request);
        } catch (Exception ex) {
            System.err.println("Failed to create fee for studentId "
                    + event.getStudentId() + ": " + ex.getMessage());
        }
    }
}