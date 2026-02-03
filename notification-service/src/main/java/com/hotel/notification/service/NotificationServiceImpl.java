package com.hotel.notification.service;

import com.hotel.notification.dto.NotificationRequest;
import com.hotel.notification.entity.Notification;
import com.hotel.notification.exception.NotificationException;
import com.hotel.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    public NotificationServiceImpl(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notification sendNotification(NotificationRequest request) {

        if (request.getType() == null || request.getRecipient() == null) {
            throw new NotificationException("Invalid notification request");
        }

        Notification notification = new Notification();
        notification.setType(request.getType());
        notification.setRecipient(request.getRecipient());
        notification.setMessage(request.getMessage());
        notification.setStatus("SENT"); // simulated

        return repository.save(notification);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return repository.findAll();
    }

    @Override
    public Notification getNotificationById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotificationException("Notification not found"));
    }
}
