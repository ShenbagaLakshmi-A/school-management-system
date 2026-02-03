package com.hotel.notification.controller;

import com.hotel.notification.dto.NotificationRequest;
import com.hotel.notification.dto.NotificationResponse;
import com.hotel.notification.entity.Notification;
import com.hotel.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public NotificationResponse send(@RequestBody NotificationRequest request) {
        Notification notification = service.sendNotification(request);

        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setStatus(notification.getStatus());

        return response;
    }

    @GetMapping
    public List<Notification> getAll() {
        return service.getAllNotifications();
    }

    @GetMapping("/{id}")
    public Notification getById(@PathVariable Long id) {
        return service.getNotificationById(id);
    }
}
