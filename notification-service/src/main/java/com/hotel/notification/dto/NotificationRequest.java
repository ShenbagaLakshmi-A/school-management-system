package com.hotel.notification.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private String type;
    private String recipient;
    private String message;
}
