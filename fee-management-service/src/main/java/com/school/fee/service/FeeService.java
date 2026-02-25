package com.school.fee.service;

import com.school.fee.dto.FeeRequest;
import com.school.fee.dto.FeeResponse;

import java.util.List;

public interface FeeService {

    FeeResponse createFee(FeeRequest request);

    List<FeeResponse> getFeesByStudent(Long studentId);

    FeeResponse markFeeAsPaid(Long feeId);
}