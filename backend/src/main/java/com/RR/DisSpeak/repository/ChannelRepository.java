package com.RR.DisSpeak.repository;

import com.RR.DisSpeak.model.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
     Channel findByName(String name);
     List<Channel> findByServer_Id(Long serverId);
}
