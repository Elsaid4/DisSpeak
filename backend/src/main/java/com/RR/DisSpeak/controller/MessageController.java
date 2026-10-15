package com.RR.DisSpeak.controller;

import com.RR.DisSpeak.dto.MessageRequest;
import com.RR.DisSpeak.model.entity.ChannelMessage;
import com.RR.DisSpeak.service.ChannelMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class MessageController {

    private final ChannelMessageService messageService;

    /** Restituisce la cronologia dei messaggi del canale specificato. */
    @GetMapping("/{channelId}/messages")
    public ResponseEntity<List<ChannelMessage>> getChannelMessages(@PathVariable Long channelId) {
        return ResponseEntity.ok(messageService.getMessagesForChannel(channelId));
    }

    /** Invia un messaggio al canale e restituisce il messaggio salvato. */
    @PostMapping("/{channelId}/messages")
    public ResponseEntity<ChannelMessage> sendMessage(
            @PathVariable Long channelId,
            @RequestBody MessageRequest request) {
        return ResponseEntity.ok(messageService.sendMessage(channelId, request.getContent(), request.getUserId()));
    }
}
