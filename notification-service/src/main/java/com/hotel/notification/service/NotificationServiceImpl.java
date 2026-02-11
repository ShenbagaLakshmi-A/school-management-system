package com.hotel.notification.service;

import com.hotel.notification.dto.NotificationRequest;
import com.hotel.notification.dto.NotificationResponse;
import com.hotel.notification.entity.Notification;
import com.hotel.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    public NotificationServiceImpl(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {

        Notification notification = new Notification();
        notification.setReservationId(request.getReservationId());
        notification.setMessage(request.getMessage());

        Notification saved = repository.save(notification);

        return mapToResponse(saved);
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found with id: " + id));

        return mapToResponse(notification);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setStatus("SENT");
        return response;
    }
}
