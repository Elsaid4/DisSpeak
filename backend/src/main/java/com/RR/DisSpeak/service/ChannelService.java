package com.RR.DisSpeak.service;

import com.RR.DisSpeak.model.entity.Channel;
import com.RR.DisSpeak.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;

    /** Recupera i canali associati al server indicato. */
    public List<Channel> getChannelsByServerId(Long serverId) {
        return channelRepository.findByServer_Id(serverId);
    }
}
