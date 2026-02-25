package com.school.student.event;

import com.school.student.config.RabbitMQConfig;
import com.school.student.entity.Student;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentCreatedEventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishStudentCreated(Student student) {
        // Create event from student entity
        StudentCreatedEvent event = new StudentCreatedEvent(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getPhone()
        );

        // Publish to RabbitMQ exchange with routing key
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.STUDENT_EXCHANGE,
                RabbitMQConfig.STUDENT_ROUTING_KEY,
                event
        );

        System.out.println("Published StudentCreatedEvent for student ID: " + student.getId());
    }
}