package com.RR.DisSpeak.service;

import com.RR.DisSpeak.dto.CreateServerDto;
import com.RR.DisSpeak.model.entity.Channel;
import com.RR.DisSpeak.model.entity.Server;
import com.RR.DisSpeak.model.entity.ServerMembers;
import com.RR.DisSpeak.model.entity.User;
import com.RR.DisSpeak.model.entity.enums.ChannelType;
import com.RR.DisSpeak.model.entity.enums.Role;
import com.RR.DisSpeak.repository.ChannelRepository;
import com.RR.DisSpeak.repository.ServerMembersRepository;
import com.RR.DisSpeak.repository.ServerRepository;
import com.RR.DisSpeak.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServerService {
    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final ServerMembersRepository serverMembersRepository;
    private final ChannelRepository channelRepository;

    /** Crea un server, assegna il creatore come amministratore e aggiunge il canale generale. */
    @Transactional
    public Server createServer(CreateServerDto request) {
        // 1. Verfica esistenza dell'utente proprietario
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + request.getOwnerId()));

        // 2. Salva il nuovo Server
        Server server = new Server();
        server.setName(request.getName());
        // Se hai aggiornato la relazione in Server come suggerito:
        server.setOwner(owner);
        // Altrimenti se hai l'id numerico semplice:
        // server.setOwner_id(owner.getId().intValue());

        Server savedServer = serverRepository.save(server);

        // 3. Aggiungi l'utente creatore nella tabella 'server_members' con ruolo ADMIN
        ServerMembers member = new ServerMembers();
        member.setServer(savedServer);
        member.setUser(owner);
        member.setRole(Role.ADMIN);
        serverMembersRepository.save(member);

        // 4. Crea il canale di testo predefinito (#generale)
        Channel defaultChannel = new Channel();
        defaultChannel.setName("generale");
        defaultChannel.setType(ChannelType.TEXT);
        defaultChannel.setPublic(true);
        defaultChannel.setServer(savedServer);
        channelRepository.save(defaultChannel);

        return savedServer;
    }

    /** Recupera i server a cui l'utente indicato risulta iscritto. */
    public List<Server> getServersByUserId(Long userId) {
        return serverMembersRepository.findAllByUserId(userId).stream()
                .map(ServerMembers::getServer)
                .toList();
    }

    /** Cerca server per nome ignorando maiuscole/minuscole ed esclude quelli già frequentati dall'utente. */
    public List<Server> searchServers(String query, Long userId) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        if (userId == null || !userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Utente non trovato: " + userId);
        }

        Set<Long> joinedServerIds = serverMembersRepository.findAllByUserId(userId).stream()
                .map(member -> member.getServer().getId())
                .collect(Collectors.toSet());

        return serverRepository.findByNameContainingIgnoreCase(query.trim()).stream()
                .filter(server -> !joinedServerIds.contains(server.getId()))
                .toList();
    }

    /** Aggiunge l'utente al server come membro, senza duplicare un'iscrizione esistente. */
    @Transactional
    public Server joinServer(Long serverId, Long userId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("Server non trovato: " + serverId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + userId));

        if (!serverMembersRepository.existsByServer_IdAndUser_Id(serverId, userId)) {
            ServerMembers member = new ServerMembers();
            member.setServer(server);
            member.setUser(user);
            member.setRole(Role.MEMBER);
            serverMembersRepository.save(member);
        }

        return server;
    }
}
