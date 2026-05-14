package com.RR.DisSpeak.model.entity;

import com.RR.DisSpeak.config.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="username")
    private String username;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="full_name")
    private String fullName;

    @Column(name="status")
    private Status status;

    public User(String username, String fullName, String email , Status status) {
        this.username   = username;
        this.fullName   = fullName;
        this.email      = email;
        this.status     = status;
    }
}
