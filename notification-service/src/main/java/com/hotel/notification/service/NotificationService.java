package com.hotel.notification.service;

import com.hotel.notification.dto.NotificationRequest;
import com.hotel.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse sendNotification(NotificationRequest request);

    List<NotificationResponse> getAllNotifications();

    NotificationResponse getNotificationById(Long id);
}
