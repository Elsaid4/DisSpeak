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

    /** Imposta l'ora corrente e salva il messaggio se è un messaggio privato. */
    @Override
    public void saveMessage(Object message) {
        if (message instanceof PrivateMessage) {
            PrivateMessage pm = (PrivateMessage) message;
            pm.setTimestamp(getCurrentTimestamp());
            privateMessageRepository.save(pm);
        }
    }

    /** Cerca un messaggio privato tramite ID e restituisce null se non esiste. */
    @Override
    public Object getMessageById(Long id) {
        Optional<PrivateMessage> message = privateMessageRepository.findById(id);
        return message.orElse(null);
    }

    /** Elimina il messaggio privato con l'ID indicato, se presente. */
    @Override
    public void deleteMessage(Long id) {
        if (privateMessageRepository.existsById(id)) {
            privateMessageRepository.deleteById(id);
        }
    }

    /** Sostituisce il contenuto del messaggio privato indicato, se presente. */
    @Override
    public void updateMessage(Long id, String newContent) {
        Optional<PrivateMessage> message = privateMessageRepository.findById(id);
        if (message.isPresent()) {
            PrivateMessage pm = message.get();
            pm.setContent(newContent);
            privateMessageRepository.save(pm);
        }
    }

    /** Recupera tutti i messaggi appartenenti alla chat privata indicata. */
    public List<PrivateMessage> getMessagesByChat(Long chatId) {
        return privateMessageRepository.findByChatId(chatId);
    }

    /** Recupera tutti i messaggi inviati dall'utente indicato. */
    public List<PrivateMessage> getMessagesBySender(int senderId) {
        return privateMessageRepository.findBySenderId(senderId);
    }

    /** Crea e salva un messaggio privato con contenuto, mittente e chat specificati. */
    public PrivateMessage createPrivateMessage(String content, User sender, PrivateChat chat) {
        PrivateMessage pm = new PrivateMessage();
        pm.setContent(content);
        pm.setSender(sender);
        pm.setChat(chat);
        pm.setTimestamp(getCurrentTimestamp());
        return privateMessageRepository.save(pm);
    }
}
