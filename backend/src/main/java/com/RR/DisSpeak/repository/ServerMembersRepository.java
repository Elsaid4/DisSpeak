package com.RR.DisSpeak.repository;

import com.RR.DisSpeak.model.entity.Server;
import com.RR.DisSpeak.model.entity.ServerMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServerMembersRepository extends JpaRepository<ServerMembers, Long> {
    List<ServerMembers> findAllByUserId(Long userId);
    boolean existsByServer_IdAndUser_Id(Long serverId, Long userId);
}
