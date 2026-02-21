package com.hotel.reservation.service;

import com.hotel.reservation.dto.ReservationRequest;
import com.hotel.reservation.dto.ReservationResponse;
import com.hotel.reservation.entity.Reservation;
import com.hotel.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceImplUnitTest {

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private NotificationClientService notificationClientService;

    @Mock
    private PaymentClientService paymentClientService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------- createReservation success --------------------
    @Test
    void testCreateReservation_PaymentSuccess() {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(1L);
        request.setHotelId(1L);
        request.setCheckIn("2026-02-20");
        request.setCheckOut("2026-02-25");
        request.setAmount(1500.0);

        // Mock Customer/Hotel validation
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());

        // Mock reservation save
        Reservation created = new Reservation();
        created.setId(100L);
        created.setCustomerId(1L);
        created.setHotelId(1L);
        created.setStatus("CREATED");
        when(reservationRepository.save(any())).thenReturn(created);

        // ✅ Mock payment client
        when(paymentClientService.processPayment(any(Reservation.class), anyDouble()))
                .thenReturn(Map.of("id", 500L, "status", "SUCCESS"));

        // Mock notification
        doNothing().when(notificationClientService).sendNotification(any(), anyString());

        // Execute
        ReservationResponse response = reservationService.createReservation(request);

        // Verify
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals(500L, response.getPaymentId());
    }

    // -------------------- createReservation payment failed --------------------
    @Test
    void testCreateReservation_PaymentFailed() {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(1L);
        request.setHotelId(1L);
        request.setCheckIn("2026-02-20");
        request.setCheckOut("2026-02-25");
        request.setAmount(1500.0);

        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());

        Reservation created = new Reservation();
        created.setId(101L);
        created.setCustomerId(1L);
        created.setHotelId(1L);
        created.setStatus("CREATED");
        when(reservationRepository.save(any())).thenReturn(created);

        when(paymentClientService.processPayment(any(Reservation.class), anyDouble()))
                .thenReturn(Map.of("id", 501L, "status", "FAILED"));

        doNothing().when(notificationClientService).sendNotification(any(), anyString());

        ReservationResponse response = reservationService.createReservation(request);

        assertEquals("FAILED", response.getStatus());
        assertNull(response.getPaymentId());
    }

    // -------------------- Customer not found --------------------
    @Test
    void testCreateReservation_CustomerNotFound() {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(99L);
        request.setHotelId(1L);

        when(restTemplate.getForObject("http://customer-service/customers/99", Object.class))
                .thenThrow(HttpClientErrorException.NotFound.class);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(request));

        assertTrue(ex.getMessage().contains("Customer not found"));
    }

    // -------------------- Hotel not found --------------------
    @Test
    void testCreateReservation_HotelNotFound() {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(1L);
        request.setHotelId(99L);

        when(restTemplate.getForObject("http://customer-service/customers/1", Object.class))
                .thenReturn(new Object());
        when(restTemplate.getForObject("http://hotel-service/hotels/99", Object.class))
                .thenThrow(HttpClientErrorException.NotFound.class);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(request));

        assertTrue(ex.getMessage().contains("Hotel not found"));
    }

    // -------------------- getReservationById --------------------
    @Test
    void testGetReservationById_Success() {
        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setCustomerId(1L);
        reservation.setHotelId(1L);
        reservation.setStatus("CONFIRMED");

        when(reservationRepository.findById(100L)).thenReturn(Optional.of(reservation));

        ReservationResponse response = reservationService.getReservationById(100L);

        assertEquals(100L, response.getReservationId());
        assertEquals("CONFIRMED", response.getStatus());
    }

    @Test
    void testGetReservationById_NotFound() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.getReservationById(999L));

        assertTrue(ex.getMessage().contains("Reservation not found"));
    }

    // -------------------- getAllReservations --------------------
    @Test
    void testGetAllReservations() {
        Reservation r1 = new Reservation();
        r1.setId(100L);
        r1.setCustomerId(1L);
        r1.setHotelId(1L);
        r1.setStatus("CONFIRMED");

        Reservation r2 = new Reservation();
        r2.setId(101L);
        r2.setCustomerId(2L);
        r2.setHotelId(2L);
        r2.setStatus("CREATED");

        when(reservationRepository.findAll()).thenReturn(List.of(r1, r2));

        List<ReservationResponse> responses = reservationService.getAllReservations();

        assertEquals(2, responses.size());
        assertEquals(100L, responses.get(0).getReservationId());
        assertEquals(101L, responses.get(1).getReservationId());
    }
}