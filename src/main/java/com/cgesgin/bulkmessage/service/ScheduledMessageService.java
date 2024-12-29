package com.cgesgin.bulkmessage.service;

import com.cgesgin.bulkmessage.model.ScheduledMessage;
import java.util.List;

public interface ScheduledMessageService {
    ScheduledMessage scheduleMessage(ScheduledMessage message);
    List<ScheduledMessage> getAllScheduledMessages();
    void processScheduledMessages();
    void deleteMessage(Long id);
} 