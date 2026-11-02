package com.RR.DisSpeak.service;

import com.RR.DisSpeak.model.entity.ChannelMessage;
import com.RR.DisSpeak.model.entity.Channel;
import com.RR.DisSpeak.model.entity.User;
import com.RR.DisSpeak.repository.ChannelMessageRepository;
import com.RR.DisSpeak.repository.ChannelRepository;
import com.RR.DisSpeak.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /** Imposta l'ora corrente e salva il messaggio se è un messaggio di canale. */
    @Override
    public void saveMessage(Object message) {
        if (message instanceof ChannelMessage) {
            ChannelMessage cm = (ChannelMessage) message;
            cm.setTimestamp(getCurrentTimestamp());
            channelMessageRepository.save(cm);
        }
    }

    /** Cerca un messaggio di canale tramite ID e restituisce null se non esiste. */
    @Override
    public Object getMessageById(Long id) {
        Optional<ChannelMessage> message = channelMessageRepository.findById(id);
        return message.orElse(null);
    }

    /** Elimina il messaggio con l'ID indicato, se presente. */
    @Override
    public void deleteMessage(Long id) {
        if (channelMessageRepository.existsById(id)) {
            channelMessageRepository.deleteById(id);
        }
    }

    /** Sostituisce il contenuto del messaggio indicato, se presente. */
    @Override
    public void updateMessage(Long id, String newContent) {
        Optional<ChannelMessage> message = channelMessageRepository.findById(id);
        if (message.isPresent()) {
            ChannelMessage cm = message.get();
            cm.setContent(newContent);
            channelMessageRepository.save(cm);
        }
    }

    /** Recupera tutti i messaggi appartenenti al canale indicato. */
    public List<ChannelMessage> getMessagesByChannel(Long channelId) {
        return channelMessageRepository.findByChannelId(channelId);
    }

    /** Recupera tutti i messaggi inviati dall'utente indicato. */
    public List<ChannelMessage> getMessagesByUser(int userId) {
        return channelMessageRepository.findByUserId(userId);
    }

    /** Crea e salva un messaggio con il contenuto, l'autore e il canale specificati. */
    public ChannelMessage createChannelMessage(String content, User user, Channel channel) {
        ChannelMessage cm = new ChannelMessage();
        cm.setContent(content);
        cm.setUser(user);
        cm.setChannel(channel);
        cm.setTimestamp(getCurrentTimestamp());
        return channelMessageRepository.save(cm);
    }

    /** Restituisce al massimo il numero di messaggi indicato per il canale. */
    public List<ChannelMessage> getLastMessagesFromChannel(Long channelId, int limit) {
        List<ChannelMessage> messages = channelMessageRepository.findByChannelId(channelId);
        return messages.stream().limit(limit).toList();
    }

    /** Recupera i messaggi del canale, usato dal controller per mostrarne la cronologia. */
    public List<ChannelMessage> getMessagesForChannel(Long channelId) {
        return channelMessageRepository.findByChannelId(channelId);
    }

    /** Crea, salva e pubblica in tempo reale un messaggio sul canale indicato. */
    public ChannelMessage sendMessage(Long channelId, String content, Long userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Canale non trovato: " + channelId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato: " + userId));

        ChannelMessage message = new ChannelMessage();
        message.setContent(content);
        message.setChannel(channel);
        message.setUser(user);
        ChannelMessage savedMessage = channelMessageRepository.save(message);
        messagingTemplate.convertAndSend("/public/channel/" + channelId, savedMessage);
        return savedMessage;
    }
}
