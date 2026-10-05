import React, { useState, useEffect } from 'react';
import axios from 'axios';
import webSocketService from '../services/websocket';

const sortMessages = (messages) => [...messages].sort(
    (a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
);

const mergeMessages = (existingMessages, incomingMessages) => {
    const messagesById = new Map(existingMessages.map(message => [message.id, message]));
    incomingMessages.forEach(message => messagesById.set(message.id, message));
    return sortMessages([...messagesById.values()]);
};

export default function Dashboard({ user, onLogout }) {
    const [servers, setServers] = useState([]);
    const [selectedServer, setSelectedServer] = useState(null);
    const [channels, setChannels] = useState([]);
    const [selectedChannel, setSelectedChannel] = useState(null);
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [loadingMessages, setLoadingMessages] = useState(false);

    // Stato per la finestra modale "Crea Server"
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [newServerName, setNewServerName] = useState('');
    const [isSearchModalOpen, setIsSearchModalOpen] = useState(false);
    const [serverSearchQuery, setServerSearchQuery] = useState('');
    const [serverSearchResults, setServerSearchResults] = useState([]);
    const [serverSearchMessage, setServerSearchMessage] = useState('');
    const [isSearchingServers, setIsSearchingServers] = useState(false);

    // 1. Carica i Server dell'utente
    useEffect(() => {
        axios.get(`/api/users/${user.id}/servers`)
            .then(res => {
                setServers(res.data);
                if (res.data.length > 0) {
                    setSelectedServer(res.data[0]);
                }
            })
            .catch(err => console.error("Errore caricamento server:", err));
    }, [user.id]);

    // 2. Carica i Canali del Server selezionato
    useEffect(() => {
        if (!selectedServer) return;

        axios.get(`/api/servers/${selectedServer.id}/channels`)
            .then(res => {
                setChannels(res.data);
                if (res.data.length > 0) {
                    setSelectedChannel(res.data[0]);
                } else {
                    setSelectedChannel(null);
                    setMessages([]);
                }
            })
            .catch(err => console.error("Errore caricamento canali:", err));
    }, [selectedServer]);

    useEffect(() => () => webSocketService.disconnect(), []);

    // 3. Carica e sottoscrive i messaggi del canale selezionato
    useEffect(() => {
        if (!selectedChannel) return;

        let isActive = true;
        setLoadingMessages(true);
        setMessages([]);

        webSocketService.subscribeToChannel(selectedChannel.id, message => {
            setMessages(prev => mergeMessages(prev, [message]));
        });
        webSocketService.connect()
            .catch(err => console.error('Errore connessione WebSocket:', err));

        axios.get(`/api/channels/${selectedChannel.id}/messages`)
            .then(({ data }) => {
                if (isActive) {
                    setMessages(prev => mergeMessages(prev, data));
                }
            })
            .catch(err => {
                console.error("Errore caricamento messaggi:", err);
            })
            .finally(() => {
                if (isActive) {
                    setLoadingMessages(false);
                }
            });

        return () => {
            isActive = false;
            webSocketService.unsubscribeFromChannel(selectedChannel.id);
        };
    }, [selectedChannel]);

    // 4. Invia un nuovo messaggio
    const handleSendMessage = (e) => {
        e.preventDefault();
        if (!newMessage.trim() || !selectedChannel) return;

        const payload = {
            content: newMessage,
            userId: user.id,
            channelId: selectedChannel.id
        };

        axios.post(`/api/channels/${selectedChannel.id}/messages`, payload)
            .then(res => {
                setMessages(prev => mergeMessages(prev, [res.data]));
                setNewMessage('');
            })
            .catch(err => console.error("Errore invio messaggio:", err));
    };

    // 5. Gestione Creazione del Server
    const handleCreateServer = (e) => {
        e.preventDefault();
        if (!newServerName.trim()) return;

        const payload = {
            name: newServerName,
            ownerId: user.id
        };

        axios.post('/api/servers', payload)
            .then(res => {
                const createdServer = res.data;
                // Aggiunge il server alla lista e lo seleziona subito
                setServers(prev => [...prev, createdServer]);
                setSelectedServer(createdServer);

                // Resetta lo stato del form e chiude il modale
                setNewServerName('');
                setIsModalOpen(false);
            })
            .catch(err => console.error("Errore creazione server:", err));
    };

    const handleSearchServers = (e) => {
        e.preventDefault();
        const query = serverSearchQuery.trim();
        if (!query) {
            setServerSearchResults([]);
            setServerSearchMessage('Inserisci il nome di un server da cercare.');
            return;
        }

        setIsSearchingServers(true);
        setServerSearchMessage('');
        axios.get('/api/servers/search', { params: { query, userId: user.id } })
            .then(res => {
                setServerSearchResults(res.data);
                setServerSearchMessage(res.data.length === 0 ? 'Nessun server trovato.' : '');
            })
            .catch(err => {
                console.error('Errore ricerca server:', err);
                setServerSearchMessage('Ricerca non riuscita. Riprova.');
            })
            .finally(() => setIsSearchingServers(false));
    };

    const handleJoinServer = (server) => {
        axios.post(`/api/servers/${server.id}/join`, { userId: user.id })
            .then(res => {
                setServers(prev => prev.some(item => item.id === res.data.id) ? prev : [...prev, res.data]);
                setSelectedServer(res.data);
                setIsSearchModalOpen(false);
                setServerSearchQuery('');
                setServerSearchResults([]);
                setServerSearchMessage('');
            })
            .catch(err => {
                console.error('Errore ingresso nel server:', err);
                setServerSearchMessage('Impossibile entrare nel server. Riprova.');
            });
    };

    return (
        <div style={styles.container}>
            {/* BARRA LATERALE 1: Lista dei Server + Pulsante Aggiungi */}
            <aside style={styles.serverBar}>
                {servers.map(server => (
                    <button
                        key={server.id}
                        onClick={() => setSelectedServer(server)}
                        style={{
                            ...styles.serverIcon,
                            borderLeft: selectedServer?.id === server.id ? '4px solid #fff' : 'none'
                        }}
                        title={server.name}
                    >
                        {server.name.substring(0, 2).toUpperCase()}
                    </button>
                ))}

                {/* Pulsante "+" per creare un nuovo server */}
                <button
                    onClick={() => setIsModalOpen(true)}
                    style={styles.addServerBtn}
                    title="Crea un nuovo server"
                >
                    +
                </button>
                <button
                    onClick={() => setIsSearchModalOpen(true)}
                    style={styles.searchServerBtn}
                    title="Cerca un server"
                    aria-label="Cerca un server"
                >
                    ?
                </button>
            </aside>

            {/* BARRA LATERALE 2: Canali del Server Selezionato */}
            <aside style={styles.channelBar}>
                <div style={styles.serverHeader}>
                    <h3 style={styles.serverName}>{selectedServer ? selectedServer.name : 'Nessun Server'}</h3>
                </div>
                <div style={styles.channelList}>
                    <div style={styles.categoryTitle}>CANALI TESTUALI</div>
                    {channels.map(channel => (
                        <div
                            key={channel.id}
                            onClick={() => setSelectedChannel(channel)}
                            style={{
                                ...styles.channelItem,
                                ...(selectedChannel?.id === channel.id ? styles.activeChannel : {})
                            }}
                        >
                            # {channel.name}
                        </div>
                    ))}
                </div>
            </aside>

            {/* AREA PRINCIPALE: Chat e Messaggi */}
            <main style={styles.mainContent}>
                <header style={styles.header}>
                    <div style={styles.headerTitle}>
                        <span style={{ color: '#8e9297', marginRight: '8px' }}>#</span>
                        <span style={{ fontWeight: 'bold' }}>{selectedChannel?.name || 'Seleziona un canale'}</span>
                    </div>

                    <div style={styles.userProfile}>
                        <span style={styles.statusDot}></span>
                        <span style={{ color: '#fff' }}>{user.fullName}</span>
                        <button onClick={onLogout} style={styles.logoutBtn}>Disconnetti</button>
                    </div>
                </header>

                <div style={styles.messagesContainer}>
                    {loadingMessages ? (
                        <p style={{ color: '#8e9297' }}>Caricamento messaggi...</p>
                    ) : messages.length === 0 ? (
                        <p style={{ color: '#8e9297' }}>Nessun messaggio presente in questo canale.</p>
                    ) : (
                        messages.map(msg => (
                            <div key={msg.id} style={styles.messageItem}>
                                <div style={styles.avatar}>
                                    {msg.user?.fullName?.charAt(0) || 'U'}
                                </div>
                                <div>
                                    <div style={styles.messageMeta}>
                                        <span style={styles.author}>{msg.user?.fullName || msg.user?.username}</span>
                                        <span style={styles.timestamp}>
                                            {new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                        </span>
                                    </div>
                                    <div style={styles.messageContent}>{msg.content}</div>
                                </div>
                            </div>
                        ))
                    )}
                </div>

                {selectedChannel && (
                    <form onSubmit={handleSendMessage} style={styles.inputArea}>
                        <input
                            type="text"
                            placeholder={`Invia un messaggio in #${selectedChannel.name}`}
                            value={newMessage}
                            onChange={(e) => setNewMessage(e.target.value)}
                            style={styles.messageInput}
                        />
                    </form>
                )}
            </main>

            {/* MODALE DI CREAZIONE SERVER */}
            {isModalOpen && (
                <div style={styles.modalOverlay}>
                    <div style={styles.modalCard}>
                        <h2 style={styles.modalTitle}>Personalizza il tuo server</h2>
                        <p style={styles.modalSubtitle}>
                            Dai al tuo nuovo server una personalità con un nome. Potrai sempre cambiarlo in seguito.
                        </p>

                        <form onSubmit={handleCreateServer}>
                            <label style={styles.label}>NOME DEL SERVER</label>
                            <input
                                type="text"
                                value={newServerName}
                                onChange={(e) => setNewServerName(e.target.value)}
                                placeholder="Es. Server di Marco"
                                style={styles.modalInput}
                                required
                                autoFocus
                            />

                            <div style={styles.modalActions}>
                                <button
                                    type="button"
                                    onClick={() => setIsModalOpen(false)}
                                    style={styles.cancelBtn}
                                >
                                    Annulla
                                </button>
                                <button type="submit" style={styles.createBtn}>
                                    Crea Server
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {isSearchModalOpen && (
                <div style={styles.modalOverlay}>
                    <div style={styles.modalCard}>
                        <h2 style={styles.modalTitle}>Cerca un server</h2>
                        <p style={styles.modalSubtitle}>Cerca per nome e unisciti a un server.</p>
                        <form onSubmit={handleSearchServers}>
                            <label style={styles.label}>NOME DEL SERVER</label>
                            <input
                                type="search"
                                value={serverSearchQuery}
                                onChange={(e) => setServerSearchQuery(e.target.value)}
                                placeholder="Cerca server..."
                                style={styles.modalInput}
                                autoFocus
                            />
                            <div style={styles.modalActions}>
                                <button
                                    type="button"
                                    onClick={() => setIsSearchModalOpen(false)}
                                    style={styles.cancelBtn}
                                >
                                    Chiudi
                                </button>
                                <button type="submit" style={styles.createBtn} disabled={isSearchingServers}>
                                    {isSearchingServers ? 'Ricerca...' : 'Cerca'}
                                </button>
                            </div>
                        </form>
                        {serverSearchMessage && <p style={styles.searchMessage}>{serverSearchMessage}</p>}
                        <div style={styles.searchResults}>
                            {serverSearchResults.map(server => (
                                <div key={server.id} style={styles.searchResult}>
                                    <span>{server.name}</span>
                                    <button
                                        type="button"
                                        style={styles.createBtn}
                                        onClick={() => handleJoinServer(server)}
                                    >
                                        Entra
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

const styles = {
    container: { display: 'flex', height: '100vh', backgroundColor: '#36393f', fontFamily: 'sans-serif', overflow: 'hidden' },
    serverBar: { width: '72px', backgroundColor: '#202225', display: 'flex', flexDirection: 'column', alignItems: 'center', paddingTop: '12px', gap: '10px' },
    serverIcon: { width: '48px', height: '48px', borderRadius: '50%', backgroundColor: '#36393f', color: '#fff', border: 'none', cursor: 'pointer', fontWeight: 'bold' },
    addServerBtn: {
        width: '48px',
        height: '48px',
        borderRadius: '50%',
        backgroundColor: '#36393f',
        color: '#23a55a',
        border: 'none',
        fontSize: '24px',
        cursor: 'pointer',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        transition: '0.2s'
    },
    searchServerBtn: {
        width: '48px',
        height: '48px',
        borderRadius: '50%',
        backgroundColor: '#36393f',
        color: '#b5bac1',
        border: 'none',
        fontSize: '20px',
        cursor: 'pointer'
    },
    channelBar: { width: '240px', backgroundColor: '#2f3136', display: 'flex', flexDirection: 'column', borderRight: '1px solid #202225' },
    serverHeader: { height: '50px', borderBottom: '1px solid #202225', display: 'flex', alignItems: 'center', padding: '0 16px' },
    serverName: { fontSize: '16px', color: '#fff', margin: 0 },
    channelList: { padding: '10px 8px', flex: 1 },
    categoryTitle: { fontSize: '12px', fontWeight: 'bold', color: '#8e9297', marginBottom: '6px', paddingLeft: '8px' },
    channelItem: { padding: '8px', borderRadius: '4px', cursor: 'pointer', color: '#8e9297', marginBottom: '2px', fontSize: '14px' },
    activeChannel: { backgroundColor: '#393c43', color: '#fff' },
    mainContent: { flex: 1, display: 'flex', flexDirection: 'column', backgroundColor: '#36393f' },
    header: { height: '50px', backgroundColor: '#36393f', borderBottom: '1px solid #202225', display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 20px' },
    headerTitle: { color: '#fff', fontSize: '16px' },
    userProfile: { display: 'flex', alignItems: 'center', gap: '10px' },
    statusDot: { width: '8px', height: '8px', borderRadius: '50%', backgroundColor: '#3ba55d' },
    logoutBtn: { padding: '6px 12px', backgroundColor: '#ed4245', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' },
    messagesContainer: { flex: 1, padding: '20px', overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '15px' },
    messageItem: { display: 'flex', gap: '12px', alignItems: 'flex-start' },
    avatar: { width: '40px', height: '40px', borderRadius: '50%', backgroundColor: '#5865f2', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold' },
    messageMeta: { display: 'flex', gap: '8px', alignItems: 'baseline', marginBottom: '4px' },
    author: { color: '#fff', fontWeight: 'bold', fontSize: '14px' },
    timestamp: { color: '#72767d', fontSize: '12px' },
    messageContent: { color: '#dcddde', fontSize: '14px', lineHeight: '1.4' },
    inputArea: { padding: '0 20px 20px 20px' },
    messageInput: { width: '100%', padding: '12px 16px', backgroundColor: '#40444b', border: 'none', borderRadius: '8px', color: '#fff', fontSize: '14px', outline: 'none' },

    /* STILI FINESTRA MODALE */
    modalOverlay: {
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.7)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 1000
    },
    modalCard: {
        backgroundColor: '#313338',
        padding: '24px',
        borderRadius: '8px',
        width: '440px',
        boxShadow: '0 8px 16px rgba(0,0,0,0.3)',
        color: '#fff'
    },
    modalTitle: { margin: '0 0 8px 0', fontSize: '20px', textAlign: 'center' },
    modalSubtitle: { color: '#b5bac1', fontSize: '14px', textAlign: 'center', marginBottom: '20px', lineHeight: '1.4' },
    label: { display: 'block', fontSize: '12px', fontWeight: 'bold', color: '#b5bac1', marginBottom: '8px' },
    modalInput: {
        width: '100%',
        padding: '10px',
        backgroundColor: '#1e1f22',
        border: 'none',
        borderRadius: '4px',
        color: '#fff',
        fontSize: '14px',
        boxSizing: 'border-box',
        marginBottom: '20px',
        outline: 'none'
    },
    modalActions: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '10px' },
    cancelBtn: { backgroundColor: 'transparent', color: '#fff', border: 'none', cursor: 'pointer', fontSize: '14px' },
    createBtn: { backgroundColor: '#5865f2', color: '#fff', border: 'none', padding: '10px 24px', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', fontSize: '14px' },
    searchMessage: { color: '#b5bac1', fontSize: '14px', margin: '16px 0 8px' },
    searchResults: { maxHeight: '240px', overflowY: 'auto' },
    searchResult: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '10px 0', color: '#fff', borderBottom: '1px solid #202225' }
};