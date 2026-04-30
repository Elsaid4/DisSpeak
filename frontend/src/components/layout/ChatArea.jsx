import React, { useState, useEffect, useRef } from 'react';
import webSocketService from '../services/websocket';
import { messageApi } from '../services/api';
import MessageList from './chat/MessageList';
import MessageInput from './chat/MessageInput';

function ChatArea({ channel, currentUser }) {
    const [messages, setMessages] = useState([]);

    useEffect(() => {
        if (!channel) return;

        // carica messaggi storici dal DB via REST
        messageApi.getByChannel(channel.id)
            .then(res => setMessages(res.data))
            .catch(err => console.error(err));

        // iscriviti ai nuovi messaggi via WebSocket
        webSocketService.subscribeToChannel(
            channel.id,
            (newMessage) => {
                setMessages(prev => [...prev, newMessage]);
            }
        );
    }, [channel]); // si riesegue ogni volta che cambia canale

    const handleSend = (content) => {
        webSocketService.sendMessage(channel.id, {
            sender: currentUser.username,
            content: content
        });
    };

    return (
        <div className="chat-area">
            <h2># {channel?.name}</h2>
            <MessageList messages={messages} />
            <MessageInput onSend={handleSend} />
        </div>
    );
}

export default ChatArea;