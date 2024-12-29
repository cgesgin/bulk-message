package com.cgesgin.bulkmessage.service.impl;

import org.springframework.stereotype.Service;


import com.cgesgin.bulkmessage.api.whatsapp.Chatting;
import com.cgesgin.bulkmessage.model.SendText;
import com.cgesgin.bulkmessage.service.ChatService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final Chatting chat;

    @Override
    public void sendMessage(String chatId, String text, int count) {
        String formattedChatId = formatChatId(chatId);

        SendText sendText = new SendText(formattedChatId, null, text, true, "default");

        for (int i = 0; i < count; i++) {
            try {
                chat.sendMessage(sendText);
                Thread.sleep(100);
            } catch (Exception e) {
                throw new RuntimeException("Mesaj gönderilirken hata oluştu: " + e.getMessage());
            }
        }
    }

    private String formatChatId(String chatId) {
        if (chatId.endsWith("@c.us") || chatId.endsWith("@g.us")) {
            return chatId;
        }

        String cleanNumber = chatId.replaceAll("[^0-9]", "");
        
        if (!cleanNumber.startsWith("90") && cleanNumber.length() == 10) {
            cleanNumber = "90" + cleanNumber;
        }
        
        return cleanNumber + "@c.us";
    }

} 