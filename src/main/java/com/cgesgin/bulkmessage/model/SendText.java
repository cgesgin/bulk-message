package com.cgesgin.bulkmessage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendText {
    private String chatId;
    private String replyTo;
    private String text;
    private boolean linkPreview;
    private String session;
}
