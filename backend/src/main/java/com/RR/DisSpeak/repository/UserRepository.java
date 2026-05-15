package com.RR.DisSpeak.repository;

import com.RR.DisSpeak.config.Status;
import com.RR.DisSpeak.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByStatus(Status status);

    Optional<User> findByUsername(String username);
    Optional<User> findByIdAndStatus(int id, Status status);
}
