import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
    constructor() {
        this.client = null;
        this.subscriptions = {};
        this.handlers = {};
        this.connectPromise = null;
    }

    connect() {
        if (this.client?.connected) {
            return Promise.resolve();
        }
        if (this.connectPromise) {
            return this.connectPromise;
        }

        this.connectPromise = new Promise((resolve, reject) => {
            const client = new Client({
                webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
                reconnectDelay: 5000,
                onConnect: () => {
                    this.subscriptions = {};
                    Object.entries(this.handlers).forEach(([channelId, handler]) => {
                        this.subscribeConnectedChannel(channelId, handler);
                    });
                    resolve();
                },
                onStompError: (frame) => {
                    reject(new Error(frame.headers.message || 'Errore WebSocket STOMP'));
                },
                onWebSocketError: () => {
                    reject(new Error('Connessione WebSocket non riuscita'));
                }
            });

            this.client = client;
            client.activate();
        }).finally(() => {
            this.connectPromise = null;
        });

        return this.connectPromise;
    }

    subscribeToChannel(channelId, onMessage) {
        const key = String(channelId);
        this.handlers[key] = onMessage;
        if (this.client?.connected) {
            this.subscribeConnectedChannel(key, onMessage);
        }
    }

    subscribeConnectedChannel(channelId, onMessage) {
        this.subscriptions[channelId]?.unsubscribe();
        this.subscriptions[channelId] = this.client.subscribe(
            `/public/channel/${channelId}`,
            (message) => onMessage(JSON.parse(message.body))
        );
    }

    unsubscribeFromChannel(channelId) {
        const key = String(channelId);
        this.subscriptions[key]?.unsubscribe();
        delete this.subscriptions[key];
        delete this.handlers[key];
    }

    disconnect() {
        if (this.client) {
            this.subscriptions = {};
            this.handlers = {};
            this.connectPromise = null;
            this.client.deactivate();
            this.client = null;
        }
    }
}

const webSocketService = new WebSocketService();

export default webSocketService;
