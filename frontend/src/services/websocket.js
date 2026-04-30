import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
    constructor() {
        this.client = null;
        this.subscriptions = {};
    }

    // connessione al server
    connect(onConnected) {
        this.client = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
            onConnect: () => {
                console.log('WebSocket connesso!');
                onConnected();
            },
            onDisconnect: () => {
                console.log('WebSocket disconnesso');
            }
        });

        this.client.activate();
    }

    // iscrizione a un canale
    subscribeToChannel(channelId, onMessage) {
        // cancella sottoscrizione precedente se esiste
        if (this.subscriptions[channelId]) {
            this.subscriptions[channelId].unsubscribe();
        }

        this.subscriptions[channelId] = this.client.subscribe(
            `/topic/channel/${channelId}`,
            (message) => {
                onMessage(JSON.parse(message.body));
            }
        );
    }

    // invio messaggio
    sendMessage(channelId, message) {
        this.client.publish({
            destination: `/app/channel/${channelId}/send`,
            body: JSON.stringify(message)
        });
    }

    // disconnessione
    disconnect() {
        if (this.client) {
            this.client.deactivate();
        }
    }
}

// esportiamo una singola istanza condivisa
export default new WebSocketService();