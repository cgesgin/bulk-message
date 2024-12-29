package com.cgesgin.bulkmessage.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScheduledMessage {
    private Long id;
    private String recipientId;
    private String recipientName;
    private String message;
    private int messageCount;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;
    private boolean sent;
    
    public ScheduledMessage() {
        this.createdAt = LocalDateTime.now();
    }
} 