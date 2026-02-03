package com.hotel.notification.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;      // EMAIL, SMS
    private String recipient; // email / phone
    private String message;

    private String status;    // SENT, FAILED
}
