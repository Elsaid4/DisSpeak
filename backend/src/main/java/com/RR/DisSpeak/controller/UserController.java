package com.RR.DisSpeak.controller;

import com.RR.DisSpeak.model.entity.Server;
import com.RR.DisSpeak.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ServerService serverService;

    @GetMapping("/{userId}/servers")
    public ResponseEntity<List<Server>> getUserServers(@PathVariable Long userId) {
        return ResponseEntity.ok(serverService.getServersByUserId(userId));
    }
}