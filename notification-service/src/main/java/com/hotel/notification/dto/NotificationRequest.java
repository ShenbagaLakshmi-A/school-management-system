package com.hotel.notification.dto;

import lombok.Data;

@Data
public class NotificationRequest {

    private Long reservationId;
    private String message;
}
