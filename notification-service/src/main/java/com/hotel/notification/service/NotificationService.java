package com.hotel.notification.service;

import com.hotel.notification.dto.NotificationRequest;
import com.hotel.notification.entity.Notification;

import java.util.List;

public interface NotificationService {

    Notification sendNotification(NotificationRequest request);

    List<Notification> getAllNotifications();

    Notification getNotificationById(Long id);
}
