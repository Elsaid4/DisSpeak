package com.RR.DisSpeak.service;

import com.RR.DisSpeak.model.entity.PrivateMessage;
import com.RR.DisSpeak.model.entity.PrivateChat;
import com.RR.DisSpeak.model.entity.User;
import com.RR.DisSpeak.repository.PrivateMessageRepository;
import com.RR.DisSpeak.repository.PrivateChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrivateMessageService extends MessageService {

    @Autowired
    private PrivateMessageRepository privateMessageRepository;

    @Autowired
    private PrivateChatRepository privateChatRepository;

    /**
     * Salva un nuovo messaggio privato
     */
    @Override
    public void saveMessage(Object message) {
        if (message instanceof PrivateMessage) {
            PrivateMessage pm = (PrivateMessage) message;
            pm.setTimestamp(getCurrentTimestamp());
            privateMessageRepository.save(pm);
        }
    }

    /**
     * Recupera un messaggio privato per ID
     */
    @Override
    public Object getMessageById(Long id) {
        Optional<PrivateMessage> message = privateMessageRepository.findById(id);
        return message.orElse(null);
    }

    /**
     * Elimina un messaggio privato (solo il proprietario può farlo)
     */
    @Override
    public void deleteMessage(Long id) {
        if (privateMessageRepository.existsById(id)) {
            privateMessageRepository.deleteById(id);
        }
    }

    /**
     * Aggiorna il contenuto di un messaggio privato
     */
    @Override
    public void updateMessage(Long id, String newContent) {
        Optional<PrivateMessage> message = privateMessageRepository.findById(id);
        if (message.isPresent()) {
            PrivateMessage pm = message.get();
            pm.setContent(newContent);
            privateMessageRepository.save(pm);
        }
    }

    /**
     * Recupera tutti i messaggi di una chat privata
     */
    public List<PrivateMessage> getMessagesByChat(Long chatId) {
        return privateMessageRepository.findByChatId(chatId);
    }

    /**
     * Recupera tutti i messaggi inviati da un utente
     */
    public List<PrivateMessage> getMessagesBySender(int senderId) {
        return privateMessageRepository.findBySenderId(senderId);
    }

    /**
     * Crea un nuovo messaggio privato con i parametri specificati
     */
    public PrivateMessage createPrivateMessage(String content, User sender, PrivateChat chat) {
        PrivateMessage pm = new PrivateMessage();
        pm.setContent(content);
        pm.setSender(sender);
        pm.setChat(chat);
        pm.setTimestamp(getCurrentTimestamp());
        return privateMessageRepository.save(pm);
    }
}

