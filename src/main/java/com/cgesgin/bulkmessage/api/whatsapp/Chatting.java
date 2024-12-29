package com.cgesgin.bulkmessage.api.whatsapp;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.cgesgin.bulkmessage.model.SendText;

@FeignClient(name = "whatsapp-chat", url = "${whatsapp.service.url}")
public interface Chatting {
    @PostMapping(value = "/api/sendtext")
    String sendMessage(@RequestBody SendText sendText);
}