package com.cgesgin.bulkmessage.controller;

import com.cgesgin.bulkmessage.model.ScheduledMessage;
import com.cgesgin.bulkmessage.service.ScheduledMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/scheduled-messages")
@RequiredArgsConstructor
public class ScheduledMessageController {

    private final ScheduledMessageService scheduledMessageService;

    @GetMapping
    public String listMessages(Model model) {
        model.addAttribute("messages", scheduledMessageService.getAllScheduledMessages());
        model.addAttribute("activeTab", "scheduled");
        return "messages/scheduled-list";
    }

    @PostMapping("/schedule")
    public String scheduleMessage(@RequestParam(required = false) String contactId,
                                @RequestParam(required = false) String contactName,
                                @RequestParam(required = false) String manualNumber,
                                @RequestParam String messageText,
                                @RequestParam int messageCount,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledTime,
                                RedirectAttributes redirectAttributes) {
        try {
            String recipientId = contactId;
            String recipientName = contactName;
            
            if (recipientId == null || recipientId.trim().isEmpty()) {
                if (manualNumber == null || manualNumber.trim().isEmpty()) {
                    throw new IllegalArgumentException("Lütfen bir alıcı seçin veya numara girin");
                }
                recipientId = manualNumber;
                recipientName = manualNumber;
            }

            ScheduledMessage message = new ScheduledMessage();
            message.setRecipientId(recipientId);
            message.setRecipientName(recipientName);
            message.setMessage(messageText);
            message.setMessageCount(messageCount);
            message.setScheduledTime(scheduledTime);

            scheduledMessageService.scheduleMessage(message);
            redirectAttributes.addFlashAttribute("success", "Mesaj başarıyla planlandı.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Mesaj planlanırken bir hata oluştu: " + e.getMessage());
        }
        return "redirect:/messages/send";
    }

    @DeleteMapping("/{id}")
    public String deleteMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            scheduledMessageService.deleteMessage(id);
            redirectAttributes.addFlashAttribute("success", "Mesaj başarıyla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Mesaj silinirken bir hata oluştu: " + e.getMessage());
        }
        return "redirect:/scheduled-messages";
    }
}