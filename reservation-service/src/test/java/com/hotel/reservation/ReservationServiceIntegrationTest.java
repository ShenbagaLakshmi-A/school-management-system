package com.hotel.reservation;

import com.hotel.reservation.dto.ReservationRequest;
import com.hotel.reservation.dto.ReservationResponse;
import com.hotel.reservation.entity.Reservation;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.service.NotificationClientService;
import com.hotel.reservation.service.PaymentClientService;
import com.hotel.reservation.service.ReservationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.datasource.username=sa",
                "spring.datasource.password=sa",
                "spring.jpa.hibernate.ddl-auto=create-drop",

                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "spring.cloud.config.enabled=false",
                "spring.cloud.compatibility-verifier.enabled=false",

                "spring.rabbitmq.listener.simple.auto-startup=false",
                "spring.rabbitmq.listener.direct.auto-startup=false",
                "spring.rabbitmq.host=localhost",
                "spring.rabbitmq.port=5672"
        }
)
class ReservationServiceIntegrationTest {

    @SpyBean
    private ReservationServiceImpl reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private PaymentClientService paymentClientService;

    @MockBean
    private NotificationClientService notificationClientService;

    @BeforeEach
    void setup() {
        reservationRepository.deleteAll();
    }

    // -------------------- createReservation Success --------------------
    @Test
    void testCreateReservation_Success() {

        when(restTemplate.getForObject("http://customer-service/customers/1", Object.class))
                .thenReturn(new Object());

        when(restTemplate.getForObject("http://hotel-service/hotels/1", Object.class))
                .thenReturn(new Object());

        when(paymentClientService.processPayment(any(), any()))
                .thenReturn(Map.of("id", 500L, "status", "SUCCESS"));

        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(1L);
        request.setHotelId(1L);
        request.setCheckIn("2026-02-20");
        request.setCheckOut("2026-02-25");
        request.setAmount(1500.0);

        ReservationResponse response = reservationService.createReservation(request);

        assertNotNull(response.getReservationId());
        assertEquals("CONFIRMED", response.getStatus());
        assertEquals(500L, response.getPaymentId());

        Reservation saved = reservationRepository
                .findById(response.getReservationId())
                .orElse(null);

        assertNotNull(saved);
        assertEquals("CONFIRMED", saved.getStatus());
    }

    // -------------------- createReservation Payment Failed --------------------
    @Test
    void testCreateReservation_PaymentFailed() {

        when(restTemplate.getForObject("http://customer-service/customers/1", Object.class))
                .thenReturn(new Object());

        when(restTemplate.getForObject("http://hotel-service/hotels/1", Object.class))
                .thenReturn(new Object());

        when(paymentClientService.processPayment(any(), any()))
                .thenReturn(Map.of("id", 501L, "status", "FAILED"));

        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(1L);
        request.setHotelId(1L);
        request.setCheckIn("2026-02-20");
        request.setCheckOut("2026-02-25");
        request.setAmount(1500.0);

        ReservationResponse response = reservationService.createReservation(request);

        assertEquals("FAILED", response.getStatus());
        assertNull(response.getPaymentId());

        Reservation saved = reservationRepository
                .findById(response.getReservationId())
                .orElse(null);

        assertNotNull(saved);
        assertEquals("FAILED", saved.getStatus());
    }

    // -------------------- Customer Not Found --------------------
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

    // -------------------- Hotel Not Found --------------------
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

    // -------------------- Get Reservation By ID --------------------
    @Test
    void testGetReservationById_Success() {

        Reservation reservation = new Reservation();
        reservation.setCustomerId(1L);
        reservation.setHotelId(1L);
        reservation.setStatus("CREATED");

        reservation = reservationRepository.save(reservation);

        ReservationResponse response =
                reservationService.getReservationById(reservation.getId());

        assertEquals(reservation.getId(), response.getReservationId());
        assertEquals("CREATED", response.getStatus());
    }

    @Test
    void testGetReservationById_NotFound() {

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.getReservationById(999L));

        assertTrue(ex.getMessage().contains("Reservation not found"));
    }
}