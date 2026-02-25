package com.school.student.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCreatedEvent {

    private Long studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}