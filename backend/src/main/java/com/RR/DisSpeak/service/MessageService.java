package com.RR.DisSpeak.service;

import java.time.LocalDateTime;

public abstract class MessageService {

    /** Salva un messaggio; l'implementazione definisce il tipo di messaggio supportato. */
    public abstract void saveMessage(Object message);

    /** Recupera un messaggio tramite ID, oppure null se non esiste. */
    public abstract Object getMessageById(Long id);

    /** Elimina il messaggio identificato dall'ID. */
    public abstract void deleteMessage(Long id);

    /** Aggiorna il contenuto del messaggio identificato dall'ID. */
    public abstract void updateMessage(Long id, String newContent);

    /** Restituisce l'ora corrente per assegnarla ai messaggi. */
    protected LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now();
    }
}
