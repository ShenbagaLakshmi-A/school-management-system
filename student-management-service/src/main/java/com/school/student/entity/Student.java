package com.school.student.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data                  // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor     // Generates no-arg constructor
@AllArgsConstructor    // Generates all-arg constructor
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private LocalDate enrollmentDate;
}