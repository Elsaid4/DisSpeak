package com.RR.DisSpeak.repository;

import com.RR.DisSpeak.model.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, Integer> {
     Server findByName(String name);
}
