package com.cgesgin.bulkmessage.service.impl;

import com.cgesgin.bulkmessage.model.ScheduledMessage;
import com.cgesgin.bulkmessage.repository.FileBasedScheduledMessageRepository;
import com.cgesgin.bulkmessage.service.ChatService;
import com.cgesgin.bulkmessage.service.ScheduledMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledMessageServiceImpl implements ScheduledMessageService {
    
    private final FileBasedScheduledMessageRepository repository;
    private final ChatService chatService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ScheduledMessageServiceImpl.class);

    @Override
    public ScheduledMessage scheduleMessage(ScheduledMessage message) {
        return repository.save(message);
    }

    @Override
    public List<ScheduledMessage> getAllScheduledMessages() {
        return repository.findAllByOrderByScheduledTimeDesc();
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void processScheduledMessages() {
        log.info("Planlanan mesajlar kontrol ediliyor...");
        List<ScheduledMessage> pendingMessages = 
            repository.findByScheduledTimeLessThanEqualAndSentFalseOrderByScheduledTimeAsc(LocalDateTime.now());
        
        if (!pendingMessages.isEmpty()) {
            log.info("{} adet bekleyen mesaj bulundu", pendingMessages.size());
        }

        for (ScheduledMessage message : pendingMessages) {
            try {
                log.info("Mesaj gönderiliyor: ID={}, Alıcı={}", message.getId(), message.getRecipientId());
                
                chatService.sendMessage(message.getRecipientId(), 
                                     message.getMessage(), 
                                     message.getMessageCount());
                
                message.setSent(true);
                repository.save(message);
                
                log.info("Mesaj başarıyla gönderildi ve durum güncellendi: ID={}", message.getId());
                
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("Mesaj gönderilirken hata oluştu: ID={}, Hata={}", 
                         message.getId(), e.getMessage(), e);
                
                message.setSent(false);
                try {
                    repository.save(message);
                } catch (Exception saveError) {
                    log.error("Hata durumu kaydedilirken problem oluştu: {}", 
                             saveError.getMessage(), saveError);
                }
            }
        }
    }

    @Override
    public void deleteMessage(Long id) {
        repository.deleteById(id);
    }
} 