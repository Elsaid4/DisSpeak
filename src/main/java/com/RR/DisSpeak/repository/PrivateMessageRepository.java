package com.RR.DisSpeak.repository;

import com.RR.DisSpeak.model.entity.PrivateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Long> {
    @Query("SELECT pm FROM PrivateMessage pm WHERE pm.chat.id = :chatId ORDER BY pm.timestamp DESC")
    List<PrivateMessage> findByChatId(@Param("chatId") Long chatId);

    @Query("SELECT pm FROM PrivateMessage pm WHERE pm.sender.id = :senderId ORDER BY pm.timestamp DESC")
    List<PrivateMessage> findBySenderId(@Param("senderId") int senderId);
}
