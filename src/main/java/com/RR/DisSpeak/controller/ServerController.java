package com.RR.DisSpeak.controller;

import com.RR.DisSpeak.dto.CreateServerDto;
import com.RR.DisSpeak.dto.JoinServerDto;
import com.RR.DisSpeak.model.entity.Channel;
import com.RR.DisSpeak.model.entity.Server;
import com.RR.DisSpeak.service.ChannelService;
import com.RR.DisSpeak.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;
    private final ChannelService channelService;

    /** Crea un server usando i dati ricevuti nel corpo della richiesta. */
    @PostMapping
    public ResponseEntity<Server> createServer(@RequestBody CreateServerDto dto) {
        return ResponseEntity.ok(serverService.createServer(dto));
    }

    /** Cerca server per nome, escludendo quelli a cui l'utente è già iscritto. */
    @GetMapping("/search")
    public ResponseEntity<List<Server>> searchServers(
            @RequestParam String query,
            @RequestParam Long userId) {
        return ResponseEntity.ok(serverService.searchServers(query, userId));
    }

    /** Iscrive l'utente indicato al server specificato. */
    @PostMapping("/{serverId}/join")
    public ResponseEntity<Server> joinServer(
            @PathVariable Long serverId,
            @RequestBody JoinServerDto dto) {
        return ResponseEntity.ok(serverService.joinServer(serverId, dto.getUserId()));
    }

    /** Restituisce l'elenco dei canali del server specificato. */
    @GetMapping("/{serverId}/channels")
    public ResponseEntity<List<Channel>> getServerChannels(@PathVariable Long serverId) {
        return ResponseEntity.ok(channelService.getChannelsByServerId(serverId));
    }
}