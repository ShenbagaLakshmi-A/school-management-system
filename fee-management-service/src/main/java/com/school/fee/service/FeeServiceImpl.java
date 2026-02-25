package com.school.fee.service;

import com.school.fee.dto.FeeRequest;
import com.school.fee.dto.FeeResponse;
import com.school.fee.dto.FeeStatus;
import com.school.fee.entity.Fee;
import com.school.fee.repository.FeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;
    private final RestTemplate restTemplate;
    private final PaymentClientService paymentClientService;

    public FeeServiceImpl(FeeRepository feeRepository,
                          RestTemplate restTemplate,
                          PaymentClientService paymentClientService) {
        this.feeRepository = feeRepository;
        this.restTemplate = restTemplate;
        this.paymentClientService = paymentClientService;
    }

    // ===================== CREATE FEE =====================
    @Override
    public FeeResponse createFee(FeeRequest request) {

        Fee fee = new Fee();
        fee.setStudentId(request.getStudentId());
        fee.setAmount(request.getAmount());
        fee.setStatus(FeeStatus.PENDING);

        // Validate student exists
        validateStudent(fee.getStudentId());

        Fee savedFee = feeRepository.save(fee);

        // 🔥 Call PaymentClientService (Circuit Breaker lives there)
        Map<String, Object> paymentResponse =
                paymentClientService.processPayment(
                        savedFee.getId(),
                        savedFee.getAmount()
                );

        if (paymentResponse != null &&
                "SUCCESS".equalsIgnoreCase(String.valueOf(paymentResponse.get("status")))) {

            savedFee.setStatus(FeeStatus.PAID);
            savedFee.setPaymentId(
                    Long.valueOf(String.valueOf(paymentResponse.get("id")))
            );
        } else {
            savedFee.setStatus(FeeStatus.FAILED);
        }

        Fee finalFee = feeRepository.save(savedFee);

        return mapToResponse(finalFee);
    }

    // ===================== GET FEES BY STUDENT =====================
    @Override
    public List<FeeResponse> getFeesByStudent(Long studentId) {
        return feeRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ===================== MARK FEE AS PAID =====================
    @Override
    public FeeResponse markFeeAsPaid(Long feeId) {
        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() ->
                        new RuntimeException("Fee not found with ID: " + feeId));

        fee.setStatus(FeeStatus.PAID);

        Fee updatedFee = feeRepository.save(fee);

        return mapToResponse(updatedFee);
    }

    // ===================== STUDENT VALIDATION =====================
    private void validateStudent(Long studentId) {

        try {
            restTemplate.getForObject(
                    "http://student-management-service/api/students/" + studentId,
                    Object.class
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("Student not found with ID: " + studentId);
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Student service unavailable: " + ex.getMessage()
            );
        }
    }

    // ===================== MAPPER =====================
    private FeeResponse mapToResponse(Fee fee) {
        FeeResponse response = new FeeResponse();
        response.setId(fee.getId());
        response.setStudentId(fee.getStudentId());
        response.setAmount(fee.getAmount());
        response.setStatus(fee.getStatus());
        response.setPaymentId(fee.getPaymentId());
        return response;
    }
}