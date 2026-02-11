package com.hotel.reservation.service;

import com.hotel.reservation.dto.ReservationRequest;
import com.hotel.reservation.dto.ReservationResponse;
import com.hotel.reservation.entity.Reservation;
import com.hotel.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestTemplate restTemplate;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  RestTemplate restTemplate) {
        this.reservationRepository = reservationRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public ReservationResponse createReservation(ReservationRequest request) {
        // Map DTO -> Entity
        Reservation reservation = new Reservation();
        reservation.setCustomerId(request.getCustomerId());
        reservation.setHotelId(request.getHotelId());
        reservation.setCheckIn(request.getCheckIn());
        reservation.setCheckOut(request.getCheckOut());

        // 1️⃣ Validate Customer
        validateCustomer(reservation.getCustomerId());

        // 2️⃣ Validate Hotel
        validateHotel(reservation.getHotelId());

        // 3️⃣ Create Reservation (initial state)
        reservation.setStatus("CREATED");
        Reservation savedReservation = reservationRepository.save(reservation);

        // 4️⃣ Trigger Payment
        Map<String, Object> paymentResponse = processPayment(savedReservation, request.getAmount());

        // 5️⃣ Update Reservation Status & capture paymentId
        if (paymentResponse != null && "SUCCESS".equalsIgnoreCase((String) paymentResponse.get("status"))) {
            savedReservation.setStatus("CONFIRMED");
            savedReservation.setPaymentId(Long.valueOf(String.valueOf(paymentResponse.get("id"))));

            // Send notification and capture notificationId
            Long notificationId = sendNotification("Reservation CONFIRMED for ID " + savedReservation.getId());
            savedReservation.setNotificationId(notificationId);

        } else {
            savedReservation.setStatus("FAILED");

            Long notificationId = sendNotification("Reservation FAILED for ID " + savedReservation.getId());
            savedReservation.setNotificationId(notificationId);
        }

        Reservation finalReservation = reservationRepository.save(savedReservation);

        // Map Entity -> DTO
        return mapToResponse(finalReservation);
    }

    @Override
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return mapToResponse(reservation);
    }

    // -------------------- Integration Helpers --------------------

    private void validateCustomer(Long customerId) {
        try {
            restTemplate.getForObject(
                    "http://customer-service:8081/customers/" + customerId,
                    Object.class
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("Customer not found with id: " + customerId);
        } catch (Exception ex) {
            throw new RuntimeException("Customer service unavailable: " + ex.getMessage());
        }
    }

    private void validateHotel(Long hotelId) {
        try {
            restTemplate.getForObject(
                    "http://hotel-service:8083/hotels/" + hotelId,
                    Object.class
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("Hotel not found with id: " + hotelId);
        } catch (Exception ex) {
            throw new RuntimeException("Hotel service unavailable: " + ex.getMessage());
        }
    }

    private Map<String, Object> processPayment(Reservation reservation, Double amount) {
        try {
            Map<String, Object> paymentRequest = Map.of(
                    "reservationId", reservation.getId(),
                    "amount", amount != null ? amount : 1000.0,
                    "status", "INITIATED"
            );

            return restTemplate.postForObject(
                    "http://payment-service:8084/payments",
                    paymentRequest,
                    Map.class
            );

        } catch (Exception ex) {
            System.err.println("Payment service failed: " + ex.getMessage());
            return null;
        }
    }

    private Long sendNotification(String message) {
        try {
            Map<String, String> notificationRequest = Map.of("message", message);

            // Capture response with notification ID
            Map<String, Object> response = restTemplate.postForObject(
                    "http://notification-service:8085/notifications",
                    notificationRequest,
                    Map.class
            );

            if (response != null && response.get("id") != null) {
                return Long.valueOf(String.valueOf(response.get("id")));
            }

        } catch (Exception ex) {
            System.err.println("Notification failed: " + ex.getMessage());
        }

        return null;
    }

    // -------------------- DTO Mapping --------------------

    private ReservationResponse mapToResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setReservationId(reservation.getId());
        response.setCustomerId(reservation.getCustomerId());
        response.setHotelId(reservation.getHotelId());
        response.setCheckIn(reservation.getCheckIn());
        response.setCheckOut(reservation.getCheckOut());
        response.setStatus(reservation.getStatus());
        response.setPaymentId(reservation.getPaymentId());
        response.setNotificationId(reservation.getNotificationId());
        return response;
    }
}
