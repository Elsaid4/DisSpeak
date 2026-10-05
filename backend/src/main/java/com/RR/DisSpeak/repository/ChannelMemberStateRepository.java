package com.RR.DisSpeak.repository;

import com.RR.DisSpeak.model.entity.ChannelMemberState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelMemberStateRepository extends JpaRepository<ChannelMemberState, Long> {
     ChannelMemberState findByChannelIdAndUserId(int channelId, int userId);
}
