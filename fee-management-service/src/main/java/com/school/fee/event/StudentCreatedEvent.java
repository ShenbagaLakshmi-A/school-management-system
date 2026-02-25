package com.school.fee.event;

import lombok.Data;

@Data
public class StudentCreatedEvent {

    private Long studentId;
    private String name;
}