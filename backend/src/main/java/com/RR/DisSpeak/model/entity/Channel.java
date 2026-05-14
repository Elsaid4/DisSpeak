package com.RR.DisSpeak.model.entity;

import com.RR.DisSpeak.config.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@Table(name = "channels")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;

    @Column(name = "is_public")
    private boolean isPublic;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;
}