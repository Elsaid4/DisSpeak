package com.RR.DisSpeak.service;

import java.time.LocalDateTime;

public abstract class MessageService {

    /**
     * Salva un nuovo messaggio
     */
    public abstract void saveMessage(Object message);

    /**
     * Recupera un messaggio per ID
     */
    public abstract Object getMessageById(Long id);

    /**
     * Elimina un messaggio
     */
    public abstract void deleteMessage(Long id);

    /**
     * Aggiorna un messaggio
     */
    public abstract void updateMessage(Long id, String newContent);

    /**
     * Metodo helper per impostare il timestamp
     */
    protected LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now();
    }
}
