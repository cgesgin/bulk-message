package com.cgesgin.bulkmessage.controller;

import com.cgesgin.bulkmessage.model.ScheduledMessage;
import com.cgesgin.bulkmessage.service.ScheduledMessageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/scheduled-messages")
@RequiredArgsConstructor
public class ScheduledMessageApiController {
    
    private final ScheduledMessageService scheduledMessageService;

    @GetMapping("/status")
    public List<MessageStatus> getMessageStatuses() {
        List<ScheduledMessage> messages = scheduledMessageService.getAllScheduledMessages();
        return messages.stream()
            .map(message -> new MessageStatus(message.getId(), message.isSent()))
            .collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    private static class MessageStatus {
        private Long id;
        private boolean sent;
    }
} 