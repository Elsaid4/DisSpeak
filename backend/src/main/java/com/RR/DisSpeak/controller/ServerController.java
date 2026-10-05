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

    @PostMapping
    public ResponseEntity<Server> createServer(@RequestBody CreateServerDto dto) {
        return ResponseEntity.ok(serverService.createServer(dto));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Server>> searchServers(
            @RequestParam String query,
            @RequestParam Long userId) {
        return ResponseEntity.ok(serverService.searchServers(query, userId));
    }

    @PostMapping("/{serverId}/join")
    public ResponseEntity<Server> joinServer(
            @PathVariable Long serverId,
            @RequestBody JoinServerDto dto) {
        return ResponseEntity.ok(serverService.joinServer(serverId, dto.getUserId()));
    }

    @GetMapping("/{serverId}/channels")
    public ResponseEntity<List<Channel>> getServerChannels(@PathVariable Long serverId) {
        return ResponseEntity.ok(channelService.getChannelsByServerId(serverId));
    }
}