package com.school.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long feeId;

    private Long studentId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // e.g., PENDING, SUCCESS, FAILED
	
	private LocalDateTime paymentDate;
}