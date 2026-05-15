package com.RR.DisSpeak.repository;

import com.RR.DisSpeak.model.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Integer> {
     Channel findByName(String name);
}
