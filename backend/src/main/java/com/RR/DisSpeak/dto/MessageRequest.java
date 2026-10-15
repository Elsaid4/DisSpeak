package com.RR.DisSpeak.dto;

import lombok.Data;

@Data
public class MessageRequest {
    private String content;
    private Long userId;
}
