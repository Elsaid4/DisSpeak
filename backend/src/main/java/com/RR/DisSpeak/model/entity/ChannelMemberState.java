package com.RR.DisSpeak.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "channel_member_state")
public class ChannelMemberState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    @Column(name = "mention_count")
    private int mentionCount = 0;

}
