package com.cgesgin.bulkmessage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cgesgin.bulkmessage.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final ChatService chatService;

    @GetMapping("/send")
    public String showSendForm(Model model) {
        return "messages/send";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam(required = false) String contactId,
                            @RequestParam(required = false) String manualNumber,
                            @RequestParam String messageText,
                            @RequestParam int messageCount,
                            RedirectAttributes redirectAttributes) {
        try {
            // Kişi ID'si veya manuel numara kontrolü
            String recipient = contactId;
            if (recipient == null || recipient.trim().isEmpty()) {
                if (manualNumber == null || manualNumber.trim().isEmpty()) {
                    throw new IllegalArgumentException("Lütfen bir alıcı seçin veya numara girin");
                }
                recipient = manualNumber;
            }

            chatService.sendMessage(recipient, messageText, messageCount);
            redirectAttributes.addFlashAttribute("success", 
                String.format("%d adet mesaj başarıyla gönderildi.", messageCount));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Mesaj gönderilirken bir hata oluştu: " + e.getMessage());
        }
        return "redirect:/messages/send";
    }
} 