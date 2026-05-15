package com.RR.DisSpeak.service;

import com.RR.DisSpeak.model.entity.ChannelMessage;
import com.RR.DisSpeak.model.entity.Channel;
import com.RR.DisSpeak.model.entity.User;
import com.RR.DisSpeak.repository.ChannelMessageRepository;
import com.RR.DisSpeak.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChannelMessageService extends MessageService {

    @Autowired
    private ChannelMessageRepository channelMessageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    /**
     * Salva un nuovo messaggio di channel
     */
    @Override
    public void saveMessage(Object message) {
        if (message instanceof ChannelMessage) {
            ChannelMessage cm = (ChannelMessage) message;
            cm.setTimestamp(getCurrentTimestamp());
            channelMessageRepository.save(cm);
        }
    }

    /**
     * Recupera un messaggio di channel per ID
     */
    @Override
    public Object getMessageById(Long id) {
        Optional<ChannelMessage> message = channelMessageRepository.findById(id);
        return message.orElse(null);
    }

    /**
     * Elimina un messaggio di channel (solo il proprietario o admin)
     */
    @Override
    public void deleteMessage(Long id) {
        if (channelMessageRepository.existsById(id)) {
            channelMessageRepository.deleteById(id);
        }
    }

    /**
     * Aggiorna il contenuto di un messaggio di channel
     */
    @Override
    public void updateMessage(Long id, String newContent) {
        Optional<ChannelMessage> message = channelMessageRepository.findById(id);
        if (message.isPresent()) {
            ChannelMessage cm = message.get();
            cm.setContent(newContent);
            channelMessageRepository.save(cm);
        }
    }

    /**
     * Recupera tutti i messaggi di un channel
     */
    public List<ChannelMessage> getMessagesByChannel(Long channelId) {
        return channelMessageRepository.findByChannelId(channelId);
    }

    /**
     * Recupera tutti i messaggi inviati da un utente
     */
    public List<ChannelMessage> getMessagesByUser(int userId) {
        return channelMessageRepository.findByUserId(userId);
    }

    /**
     * Crea un nuovo messaggio di channel con i parametri specificati
     */
    public ChannelMessage createChannelMessage(String content, User user, Channel channel) {
        ChannelMessage cm = new ChannelMessage();
        cm.setContent(content);
        cm.setUser(user);
        cm.setChannel(channel);
        cm.setTimestamp(getCurrentTimestamp());
        return channelMessageRepository.save(cm);
    }

    /**
     * Recupera gli ultimi N messaggi di un channel
     */
    public List<ChannelMessage> getLastMessagesFromChannel(Long channelId, int limit) {
        List<ChannelMessage> messages = channelMessageRepository.findByChannelId(channelId);
        return messages.stream().limit(limit).toList();
    }
}

