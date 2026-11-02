package com.RR.DisSpeak.repository;

import com.RR.DisSpeak.model.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServerRepository extends JpaRepository<Server, Long> {
     Server findByName(String name);
     List<Server> findByNameContainingIgnoreCase(String name);
}
