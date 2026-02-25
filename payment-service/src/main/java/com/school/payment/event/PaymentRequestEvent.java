package com.school.payment.event;

import java.math.BigDecimal;

public class PaymentRequestEvent {

    private Long feeId;
    private Long studentId;
    private BigDecimal amount;

    public PaymentRequestEvent() {}

    public Long getFeeId() {
        return feeId;
    }

    public void setFeeId(Long feeId) {
        this.feeId = feeId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}