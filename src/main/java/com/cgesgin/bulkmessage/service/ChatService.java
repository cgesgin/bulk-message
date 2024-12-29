package com.cgesgin.bulkmessage.service;


public interface ChatService {
    void sendMessage(String chatId, String text, int count);
} 