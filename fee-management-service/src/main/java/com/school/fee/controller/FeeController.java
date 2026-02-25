package com.school.fee.controller;

import com.school.fee.dto.FeeRequest;
import com.school.fee.dto.FeeResponse;
import com.school.fee.service.FeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fees")
public class FeeController {

    private final FeeService feeService;

    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    // ================= CREATE FEE =================
    @PostMapping
    public FeeResponse createFee(@RequestBody FeeRequest request) {
        return feeService.createFee(request);
    }

    // ================= GET FEES BY STUDENT =================
    @GetMapping("/student/{studentId}")
    public List<FeeResponse> getFeesByStudent(@PathVariable Long studentId) {
        return feeService.getFeesByStudent(studentId);
    }

    // ================= MARK AS PAID =================
    @PutMapping("/{feeId}/pay")
    public FeeResponse markFeeAsPaid(@PathVariable Long feeId) {
        return feeService.markFeeAsPaid(feeId);
    }
}