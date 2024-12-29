package com.cgesgin.bulkmessage.repository;

import com.cgesgin.bulkmessage.model.ScheduledMessage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class FileBasedScheduledMessageRepository {
    
    @Value("${app.storage.file-path:scheduled_messages.json}")
    private String filePath;
    
    private final ObjectMapper objectMapper;
    private List<ScheduledMessage> messages;
    private AtomicLong idCounter;

    public FileBasedScheduledMessageRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.messages = new ArrayList<>();
        this.idCounter = new AtomicLong(1);
    }

    @PostConstruct
    public void init() {
        loadMessages();
    }

    public synchronized ScheduledMessage save(ScheduledMessage message) {
        loadMessages();
        
        if (message.getId() == null) {
            message.setId(idCounter.getAndIncrement());
            messages.add(message);
        } else {
            int index = -1;
            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).getId().equals(message.getId())) {
                    index = i;
                    break;
                }
            }
            
            if (index != -1) {
                messages.set(index, message);
            } else {
                messages.add(message);
            }
        }
        
        saveMessages();
        return message;
    }

    public List<ScheduledMessage> findAllByOrderByScheduledTimeDesc() {
        loadMessages();
        messages.sort((m1, m2) -> m2.getScheduledTime().compareTo(m1.getScheduledTime()));
        return new ArrayList<>(messages);
    }

    public List<ScheduledMessage> findByScheduledTimeLessThanEqualAndSentFalseOrderByScheduledTimeAsc(LocalDateTime now) {
        loadMessages();
        List<ScheduledMessage> pendingMessages = new ArrayList<>();
        for (ScheduledMessage message : messages) {
            if (!message.isSent() && message.getScheduledTime().compareTo(now) <= 0) {
                pendingMessages.add(message);
            }
        }
        pendingMessages.sort((m1, m2) -> m1.getScheduledTime().compareTo(m2.getScheduledTime()));
        return pendingMessages;
    }

    private synchronized void loadMessages() {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            try {
                messages = objectMapper.readValue(file, new TypeReference<List<ScheduledMessage>>() {});
                long maxId = messages.stream()
                    .mapToLong(ScheduledMessage::getId)
                    .max()
                    .orElse(0);
                idCounter.set(maxId + 1);
            } catch (IOException e) {
                messages = new ArrayList<>();
            }
        } else {
            messages = new ArrayList<>();
            try {
                saveMessages();
            } catch (Exception e) {
            }
        }
    }

    private synchronized void saveMessages() {
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            objectMapper.writeValue(file, messages);
        } catch (IOException e) {
            throw new RuntimeException("Mesajlar kaydedilirken hata oluştu", e);
        }
    }

    public void deleteById(Long id) {
        loadMessages();
        messages.removeIf(m -> m.getId().equals(id));
        saveMessages();
    }
} 