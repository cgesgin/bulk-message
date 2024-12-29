package com.cgesgin.bulkmessage.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cgesgin.bulkmessage.model.ScheduledMessage;
import com.cgesgin.bulkmessage.service.ChatService;
import com.cgesgin.bulkmessage.service.ScheduledMessageService;


import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final ChatService chatService;
    private final ScheduledMessageService scheduledMessageService;

    @GetMapping("/send")
    public String showSendForm(Model model) {
        model.addAttribute("activeTab", "send");
        return "messages/send";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam(required = false) String contactId,
                            @RequestParam(required = false) String manualNumber,
                            @RequestParam String messageText,
                            @RequestParam int messageCount,
                            @RequestParam(required = false) String action,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledTime,
                            RedirectAttributes redirectAttributes) {
        try {
            String recipientId = contactId;
            if (recipientId == null || recipientId.trim().isEmpty()) {
                if (manualNumber == null || manualNumber.trim().isEmpty()) {
                    throw new IllegalArgumentException("Lütfen bir alıcı seçin veya numara girin");
                }
                recipientId = manualNumber;
            }
            if ("schedule".equals(action) && scheduledTime != null) {
                ScheduledMessage message = new ScheduledMessage();
                message.setRecipientId(recipientId);
                message.setRecipientName(recipientId);
                message.setMessage(messageText);
                message.setMessageCount(messageCount);
                message.setScheduledTime(scheduledTime);
                
                scheduledMessageService.scheduleMessage(message);
                redirectAttributes.addFlashAttribute("success", "Mesaj başarıyla planlandı.");
                return "redirect:/scheduled-messages";
            } else {
                chatService.sendMessage(recipientId, messageText, messageCount);
                redirectAttributes.addFlashAttribute("success", 
                    String.format("%d adet mesaj başarıyla gönderildi.", messageCount));
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Mesaj gönderilirken bir hata oluştu: " + e.getMessage());
        }
        return "redirect:/messages/send";
    }
} 