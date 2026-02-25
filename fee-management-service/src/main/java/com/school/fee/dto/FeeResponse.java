package com.school.fee.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FeeResponse {

    private Long id;
    private Long studentId;
    private BigDecimal amount;
    private FeeStatus status;
    private Long paymentId;
}