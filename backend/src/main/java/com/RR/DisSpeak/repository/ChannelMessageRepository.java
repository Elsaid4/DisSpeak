package com.RR.DisSpeak.repository;

import com.RR.DisSpeak.model.entity.ChannelMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelMessageRepository extends JpaRepository<ChannelMessage, Long> {
    @Query("SELECT cm FROM ChannelMessage cm WHERE cm.channel.id = :channelId ORDER BY cm.timestamp DESC")
    List<ChannelMessage> findByChannelId(@Param("channelId") Long channelId);

    @Query("SELECT cm FROM ChannelMessage cm WHERE cm.user.id = :userId ORDER BY cm.timestamp DESC")
    List<ChannelMessage> findByUserId(@Param("userId") int userId);
}
