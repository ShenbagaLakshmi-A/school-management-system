package com.school.student.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate enrollmentDate;
}