package com.cgesgin.bulkmessage.service;


public interface ChatService {
    void sendMessage(String recipient, String message, int count);
} 