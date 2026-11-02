package com.RR.DisSpeak.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "servers")
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    //private String icon;

    public Server(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }
}
