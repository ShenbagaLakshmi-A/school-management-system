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
    private final NotificationClientService notificationClientService;
    private final PaymentClientService paymentClientService;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  RestTemplate restTemplate,
                                  NotificationClientService notificationClientService,
                                  PaymentClientService paymentClientService) {
        this.reservationRepository = reservationRepository;
        this.restTemplate = restTemplate;
        this.notificationClientService = notificationClientService;
        this.paymentClientService = paymentClientService;
    }

    @Override
	public ReservationResponse createReservation(ReservationRequest request) {
		// -------------------- Map request to entity --------------------
		Reservation reservation = new Reservation();
		reservation.setCustomerId(request.getCustomerId());
		reservation.setHotelId(request.getHotelId());
		reservation.setCheckIn(request.getCheckIn());
		reservation.setCheckOut(request.getCheckOut());
		reservation.setStatus("CREATED");

		// -------------------- Validate Customer & Hotel --------------------
		validateCustomer(reservation.getCustomerId());
		validateHotel(reservation.getHotelId());

		// -------------------- Save initial reservation --------------------
		Reservation savedReservation = reservationRepository.save(reservation);

		// -------------------- Payment --------------------
		// Let exceptions propagate to Resilience4j
		Map<String, Object> paymentResponse = paymentClientService.processPayment(savedReservation, request.getAmount());

		// -------------------- Update reservation status --------------------
		if (paymentResponse != null && "SUCCESS".equalsIgnoreCase(String.valueOf(paymentResponse.get("status")))) {
			savedReservation.setStatus("CONFIRMED");
			savedReservation.setPaymentId(Long.valueOf(String.valueOf(paymentResponse.get("id"))));
		} else {
			savedReservation.setStatus("FAILED");
		}

		// -------------------- Notification --------------------
		notificationClientService.sendNotification(
				savedReservation,
				"Reservation " + savedReservation.getStatus() +
						" for ID " + savedReservation.getId()
		);

		// -------------------- Final save --------------------
		Reservation finalReservation = reservationRepository.save(savedReservation);

		// -------------------- Map to DTO --------------------
		return mapToResponse(finalReservation);
	}
	

    @Override
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + id));
        return mapToResponse(reservation);
    }

    @Override
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateCustomer(Long customerId) {
        try {
            restTemplate.getForObject(
                    "http://customer-service/customers/" + customerId,
                    Object.class
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("Customer not found with ID: " + customerId);
        } catch (Exception ex) {
            throw new RuntimeException("Customer service unavailable: " + ex.getMessage());
        }
    }

    private void validateHotel(Long hotelId) {
        try {
            restTemplate.getForObject(
                    "http://hotel-service/hotels/" + hotelId,
                    Object.class
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("Hotel not found with ID: " + hotelId);
        } catch (Exception ex) {
            throw new RuntimeException("Hotel service unavailable: " + ex.getMessage());
        }
    }

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