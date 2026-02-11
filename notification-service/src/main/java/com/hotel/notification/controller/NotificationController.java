package com.hotel.notification.controller;

import com.hotel.notification.dto.NotificationRequest;
import com.hotel.notification.dto.NotificationResponse;
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
    public NotificationResponse sendNotification(
            @RequestBody NotificationRequest request) {
        return service.sendNotification(request);
    }

    @GetMapping
    public List<NotificationResponse> getAllNotifications() {
        return service.getAllNotifications();
    }

    @GetMapping("/{id}")
    public NotificationResponse getNotificationById(@PathVariable Long id) {
        return service.getNotificationById(id);
    }
}
